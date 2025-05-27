export type Json =
  | string
  | number
  | boolean
  | null
  | { [key: string]: Json | undefined }
  | Json[]

export type Database = {
  public: {
    Tables: {
      customers: {
        Row: {
          address: string
          created_at: string
          customer_id: string
          email: string
          name: string
          phone: string
        }
        Insert: {
          address: string
          created_at?: string
          customer_id?: string
          email: string
          name: string
          phone: string
        }
        Update: {
          address?: string
          created_at?: string
          customer_id?: string
          email?: string
          name?: string
          phone?: string
        }
        Relationships: []
      }
      order_history: {
        Row: {
          comments: string
          history_id: string
          order_id: string
          status: string
          timestamp: string
        }
        Insert: {
          comments: string
          history_id?: string
          order_id: string
          status: string
          timestamp?: string
        }
        Update: {
          comments?: string
          history_id?: string
          order_id?: string
          status?: string
          timestamp?: string
        }
        Relationships: [
          {
            foreignKeyName: "order_history_order_id_fkey"
            columns: ["order_id"]
            isOneToOne: false
            referencedRelation: "orders"
            referencedColumns: ["order_id"]
          },
        ]
      }
      orders: {
        Row: {
          appointment_date: string
          assigned_employee_id: string | null
          created_at: string
          customer_id: string
          description: string
          order_id: string
          status: string
          total: number
          vehicle_id: string
        }
        Insert: {
          appointment_date: string
          assigned_employee_id?: string | null
          created_at?: string
          customer_id: string
          description: string
          order_id?: string
          status: string
          total: number
          vehicle_id: string
        }
        Update: {
          appointment_date?: string
          assigned_employee_id?: string | null
          created_at?: string
          customer_id?: string
          description?: string
          order_id?: string
          status?: string
          total?: number
          vehicle_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "orders_customer_id_fkey"
            columns: ["customer_id"]
            isOneToOne: false
            referencedRelation: "customers"
            referencedColumns: ["customer_id"]
          },
          {
            foreignKeyName: "orders_vehicle_id_fkey"
            columns: ["vehicle_id"]
            isOneToOne: false
            referencedRelation: "vehicles"
            referencedColumns: ["vehicle_id"]
          },
        ]
      }
      profiles: {
        Row: {
          avatar_url: string | null
          email: string | null
          full_name: string | null
          id: string
          updated_at: string | null
        }
        Insert: {
          avatar_url?: string | null
          email?: string | null
          full_name?: string | null
          id: string
          updated_at?: string | null
        }
        Update: {
          avatar_url?: string | null
          email?: string | null
          full_name?: string | null
          id?: string
          updated_at?: string | null
        }
        Relationships: []
      }
      reception_images: {
        Row: {
          created_at: string
          image_id: string
          image_url: string
          order_id: string
        }
        Insert: {
          created_at?: string
          image_id?: string
          image_url: string
          order_id: string
        }
        Update: {
          created_at?: string
          image_id?: string
          image_url?: string
          order_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "reception_images_order_id_fkey"
            columns: ["order_id"]
            isOneToOne: false
            referencedRelation: "orders"
            referencedColumns: ["order_id"]
          },
        ]
      }
      reception_reports: {
        Row: {
          entry_date: string
          gas_level: number
          mileage: number
          observations: string
          order_id: string
          report_id: string
          tow_truck: boolean
          vin: string
          work_description: string
        }
        Insert: {
          entry_date?: string
          gas_level: number
          mileage: number
          observations: string
          order_id: string
          report_id?: string
          tow_truck: boolean
          vin: string
          work_description: string
        }
        Update: {
          entry_date?: string
          gas_level?: number
          mileage?: number
          observations?: string
          order_id?: string
          report_id?: string
          tow_truck?: boolean
          vin?: string
          work_description?: string
        }
        Relationships: [
          {
            foreignKeyName: "reception_reports_order_id_fkey"
            columns: ["order_id"]
            isOneToOne: false
            referencedRelation: "orders"
            referencedColumns: ["order_id"]
          },
        ]
      }
      vehicle_inventory: {
        Row: {
          ac: boolean
          directionals: boolean
          gas_cap: boolean
          headlights: boolean
          horn: boolean
          inventory_id: string
          keys: boolean
          locks: boolean
          mirrors: boolean
          rear_lights: boolean
          report_id: string
          screens: boolean
          seats: boolean
          speakers: boolean
          sunroof: boolean
          third_brake_light: boolean
          windows: boolean
          wipers: boolean
        }
        Insert: {
          ac: boolean
          directionals: boolean
          gas_cap: boolean
          headlights: boolean
          horn: boolean
          inventory_id?: string
          keys: boolean
          locks: boolean
          mirrors: boolean
          rear_lights: boolean
          report_id: string
          screens: boolean
          seats: boolean
          speakers: boolean
          sunroof: boolean
          third_brake_light: boolean
          windows: boolean
          wipers: boolean
        }
        Update: {
          ac?: boolean
          directionals?: boolean
          gas_cap?: boolean
          headlights?: boolean
          horn?: boolean
          inventory_id?: string
          keys?: boolean
          locks?: boolean
          mirrors?: boolean
          rear_lights?: boolean
          report_id?: string
          screens?: boolean
          seats?: boolean
          speakers?: boolean
          sunroof?: boolean
          third_brake_light?: boolean
          windows?: boolean
          wipers?: boolean
        }
        Relationships: [
          {
            foreignKeyName: "vehicle_inventory_report_id_fkey"
            columns: ["report_id"]
            isOneToOne: false
            referencedRelation: "reception_reports"
            referencedColumns: ["report_id"]
          },
        ]
      }
      vehicles: {
        Row: {
          created_at: string
          license_plate: string
          make: string
          model: string
          owner_id: string
          vehicle_id: string
          year: number
        }
        Insert: {
          created_at?: string
          license_plate: string
          make: string
          model: string
          owner_id: string
          vehicle_id?: string
          year: number
        }
        Update: {
          created_at?: string
          license_plate?: string
          make?: string
          model?: string
          owner_id?: string
          vehicle_id?: string
          year?: number
        }
        Relationships: [
          {
            foreignKeyName: "vehicles_owner_id_fkey"
            columns: ["owner_id"]
            isOneToOne: false
            referencedRelation: "customers"
            referencedColumns: ["customer_id"]
          },
        ]
      }
      warning_lights: {
        Row: {
          airbag: boolean
          battery: boolean
          check_engine: boolean
          exterior_lights_out: boolean
          hand_brake: boolean
          oil: boolean
          report_id: string
          tire_pressure: boolean
          traction_control: boolean
          transmission: boolean
          warning_id: string
        }
        Insert: {
          airbag: boolean
          battery: boolean
          check_engine: boolean
          exterior_lights_out: boolean
          hand_brake: boolean
          oil: boolean
          report_id: string
          tire_pressure: boolean
          traction_control: boolean
          transmission: boolean
          warning_id?: string
        }
        Update: {
          airbag?: boolean
          battery?: boolean
          check_engine?: boolean
          exterior_lights_out?: boolean
          hand_brake?: boolean
          oil?: boolean
          report_id?: string
          tire_pressure?: boolean
          traction_control?: boolean
          transmission?: boolean
          warning_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "warning_lights_report_id_fkey"
            columns: ["report_id"]
            isOneToOne: false
            referencedRelation: "reception_reports"
            referencedColumns: ["report_id"]
          },
        ]
      }
    }
    Views: {
      [_ in never]: never
    }
    Functions: {
      [_ in never]: never
    }
    Enums: {
      [_ in never]: never
    }
    CompositeTypes: {
      [_ in never]: never
    }
  }
}

type PublicSchema = Database[Extract<keyof Database, "public">]

export type Tables<
  PublicTableNameOrOptions extends
    | keyof (PublicSchema["Tables"] & PublicSchema["Views"])
    | { schema: keyof Database },
  TableName extends PublicTableNameOrOptions extends { schema: keyof Database }
    ? keyof (Database[PublicTableNameOrOptions["schema"]]["Tables"] &
        Database[PublicTableNameOrOptions["schema"]]["Views"])
    : never = never,
> = PublicTableNameOrOptions extends { schema: keyof Database }
  ? (Database[PublicTableNameOrOptions["schema"]]["Tables"] &
      Database[PublicTableNameOrOptions["schema"]]["Views"])[TableName] extends {
      Row: infer R
    }
    ? R
    : never
  : PublicTableNameOrOptions extends keyof (PublicSchema["Tables"] &
        PublicSchema["Views"])
    ? (PublicSchema["Tables"] &
        PublicSchema["Views"])[PublicTableNameOrOptions] extends {
        Row: infer R
      }
      ? R
      : never
    : never

export type TablesInsert<
  PublicTableNameOrOptions extends
    | keyof PublicSchema["Tables"]
    | { schema: keyof Database },
  TableName extends PublicTableNameOrOptions extends { schema: keyof Database }
    ? keyof Database[PublicTableNameOrOptions["schema"]]["Tables"]
    : never = never,
> = PublicTableNameOrOptions extends { schema: keyof Database }
  ? Database[PublicTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Insert: infer I
    }
    ? I
    : never
  : PublicTableNameOrOptions extends keyof PublicSchema["Tables"]
    ? PublicSchema["Tables"][PublicTableNameOrOptions] extends {
        Insert: infer I
      }
      ? I
      : never
    : never

export type TablesUpdate<
  PublicTableNameOrOptions extends
    | keyof PublicSchema["Tables"]
    | { schema: keyof Database },
  TableName extends PublicTableNameOrOptions extends { schema: keyof Database }
    ? keyof Database[PublicTableNameOrOptions["schema"]]["Tables"]
    : never = never,
> = PublicTableNameOrOptions extends { schema: keyof Database }
  ? Database[PublicTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Update: infer U
    }
    ? U
    : never
  : PublicTableNameOrOptions extends keyof PublicSchema["Tables"]
    ? PublicSchema["Tables"][PublicTableNameOrOptions] extends {
        Update: infer U
      }
      ? U
      : never
    : never

export type Enums<
  PublicEnumNameOrOptions extends
    | keyof PublicSchema["Enums"]
    | { schema: keyof Database },
  EnumName extends PublicEnumNameOrOptions extends { schema: keyof Database }
    ? keyof Database[PublicEnumNameOrOptions["schema"]]["Enums"]
    : never = never,
> = PublicEnumNameOrOptions extends { schema: keyof Database }
  ? Database[PublicEnumNameOrOptions["schema"]]["Enums"][EnumName]
  : PublicEnumNameOrOptions extends keyof PublicSchema["Enums"]
    ? PublicSchema["Enums"][PublicEnumNameOrOptions]
    : never

export type CompositeTypes<
  PublicCompositeTypeNameOrOptions extends
    | keyof PublicSchema["CompositeTypes"]
    | { schema: keyof Database },
  CompositeTypeName extends PublicCompositeTypeNameOrOptions extends {
    schema: keyof Database
  }
    ? keyof Database[PublicCompositeTypeNameOrOptions["schema"]]["CompositeTypes"]
    : never = never,
> = PublicCompositeTypeNameOrOptions extends { schema: keyof Database }
  ? Database[PublicCompositeTypeNameOrOptions["schema"]]["CompositeTypes"][CompositeTypeName]
  : PublicCompositeTypeNameOrOptions extends keyof PublicSchema["CompositeTypes"]
    ? PublicSchema["CompositeTypes"][PublicCompositeTypeNameOrOptions]
    : never
