import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import './index.css'; // <-- Esto es lo que falta
import { GoogleOAuthProvider } from "@react-oauth/google";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <GoogleOAuthProvider clientId="620904658382-u0nhrdtispsvvmsdglrfjl3qp92h3m9s.apps.googleusercontent.com">
    <React.StrictMode>
      <App />
    </React.StrictMode>
  </GoogleOAuthProvider>
);
