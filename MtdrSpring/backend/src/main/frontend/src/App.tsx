
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import DeveloperDashboard from "./pages/DeveloperDashboard";
import ProjectDetails from "./pages/ProjectDetails";
import NotFound from "./pages/NotFound";
import Index from "./pages/Index";
import Reports from "./pages/Reports";
import Register from "./pages/Register";

// Create a new QueryClient instance outside the component
const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <TooltipProvider>
          <Routes>
            <Route path="/" element={<Index />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/developer-dashboard" element={<DeveloperDashboard />} />
            <Route path="/projects/:projectId" element={<ProjectDetails />} />
            <Route path="/sprints" element={<Navigate to="/sprints/active" />} />
            <Route path="/reports" element={<Reports />} />
            <Route path="*" element={<NotFound />} />
            <Route path="/register" element={<Register />} />
          </Routes>
          <Toaster />
          <Sonner />
        </TooltipProvider>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

export default App;
