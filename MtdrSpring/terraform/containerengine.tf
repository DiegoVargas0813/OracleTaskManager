data "oci_containerengine_cluster_option" "mtdrworkshop_cluster_option" {
  cluster_option_id = "all"
}

data "oci_containerengine_node_pool_option" "mtdrworkshop_node_pool_option" {
  node_pool_option_id = "all"
}

locals {
  versions = reverse(sort(data.oci_containerengine_cluster_option.mtdrworkshop_cluster_option.kubernetes_versions))
  latest   = local.versions[0]

  # All available sources
  all_sources = data.oci_containerengine_node_pool_option.mtdrworkshop_node_pool_option.sources

  # Get only Oracle Linux images that match latest Kubernetes version
  oracle_linux_images = [
    for source in local.all_sources : source if (
      source.source_name != null &&
      length(regexall("Oracle-Linux-[0-9]*.[0-9]*-20[0-9]*", source.source_name)) > 0 &&
      source.kubernetes_version == local.latest
    )
  ]
}

resource "oci_containerengine_cluster" "mtdrworkshop_cluster" {
  compartment_id = var.ociCompartmentOcid
  name           = "mtdrworkshopcluster-${var.mtdrKey}"
  kubernetes_version = local.latest
  vcn_id         = oci_core_vcn.okevcn.id

  endpoint_config {
    is_public_ip_enabled = "true"
    subnet_id            = oci_core_subnet.endpoint.id
  }

  options {
    service_lb_subnet_ids = [oci_core_subnet.svclb_Subnet.id]

    add_ons {
      is_kubernetes_dashboard_enabled = "false"
      is_tiller_enabled               = "false"
    }

    admission_controller_options {
      is_pod_security_policy_enabled = "false"
    }

    kubernetes_network_config {
      pods_cidr     = "10.244.0.0/16"
      services_cidr = "10.96.0.0/16"
    }
  }
}

resource "oci_containerengine_node_pool" "oke_node_pool" {
  cluster_id         = oci_containerengine_cluster.mtdrworkshop_cluster.id
  compartment_id     = var.ociCompartmentOcid
  name               = "Pool"
  kubernetes_version = local.latest
  node_shape         = "VM.Standard2.2"

  node_config_details {
    size = 3
    placement_configs {
      availability_domain = data.oci_identity_availability_domain.ad1.name
      subnet_id           = oci_core_subnet.nodePool_Subnet.id
    }
  }

  node_source_details {
    source_type = "IMAGE"
    image_id    = local.oracle_linux_images[0].image_id
  }
}
