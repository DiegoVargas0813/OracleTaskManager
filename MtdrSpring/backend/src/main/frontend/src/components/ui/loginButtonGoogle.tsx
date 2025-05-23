import { GoogleLogin, CredentialResponse } from "@react-oauth/google";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";

export default function GoogleLoginButton() {
  const navigate = useNavigate();

  const handleLoginSuccess = async (response: CredentialResponse) => {
    const idToken = response.credential;

    if (!idToken) {
      toast.error("No se recibió token de Google");
      return;
    }

    try {
      const res = await axios.post("http://localhost:8081/auth/google", {
        idToken,
      });

      const jwt = res.data.jwt;

      if (jwt) {
        localStorage.setItem("jwt", jwt);
        toast.success("Inicio de sesión con Google exitoso");
        navigate("/dashboard");
      } else {
        toast.error("El backend no devolvió un token");
      }

    } catch (error: any) {
      console.error("Error al autenticar con Google:", error.response?.data || error.message);
      toast.error("Error al iniciar sesión con Google");
    }
  };

  return (
    <GoogleLogin
      onSuccess={handleLoginSuccess}
      onError={() => toast.error("Falló el login con Google")}
    />
  );
}
