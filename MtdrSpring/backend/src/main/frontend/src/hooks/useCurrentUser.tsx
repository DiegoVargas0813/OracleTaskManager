
import { useQuery } from '@tanstack/react-query';
import { toast } from 'sonner';
import { User } from '@/types/models';

// Mock current user for development
const mockCurrentUser: User = {
  id: 1,
  name: "Abraham Chávez",
  role: "Project Manager",
  email: "abraham.chavez@company.com",
  creation_ts: "2023-01-10T08:00:00Z"
};

export const useCurrentUser = () => {
  return useQuery({
    queryKey: ['currentUser'],
    queryFn: async (): Promise<User> => {
      try {
        // Usar la URL específica del entorno para la API
        const apiUrl = import.meta.env.VITE_API_URL || '';
        console.log('Fetching current user from:', `${apiUrl}/me`);
        
        // Intentar conexión sin caché para evitar problemas
        const response = await fetch(`${apiUrl}/me`, {
          cache: 'no-store',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          }
        });
        
        if (!response.ok) {
          console.warn(`API returned ${response.status} for current user. URL: ${apiUrl}/me`);
          // En caso de error, usar datos de prueba sin mostrar error al usuario
          return mockCurrentUser;
        }
        
        const data = await response.json();
        return data as User;
      } catch (error) {
        console.error('Error fetching current user from API:', error);
        // En desarrollo, siempre retornar el usuario mock para evitar bloqueos
        return mockCurrentUser;
      }
    },
    retry: 2,
    staleTime: 15000, // 15 segundos
    meta: {
      onError: () => {
        // Silenciar notificaciones de error para mejorar UX
        console.log('Usando datos de usuario de prueba');
      }
    }
  });
};
