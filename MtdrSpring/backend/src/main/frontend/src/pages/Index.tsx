import GoogleLoginButton from '@/components/ui/loginButtonGoogle';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import RegisterButton from '@/components/ui/RegisterButton';
import { 
  Card, 
  CardHeader, 
  CardTitle, 
  CardDescription,
  CardContent,
  CardFooter
} from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { toast } from 'sonner';
import { LockKeyhole, Mail } from 'lucide-react';

const Index = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
  
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
      });
  
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Error en la solicitud');
      }
  
      const data = await response.json();

      if (data && data.jwt && data.role) {
        localStorage.setItem('jwt', data.jwt);
        localStorage.setItem('user', JSON.stringify({
          email,
          name: data.name,
          role: data.role
        }));
        toast.success('Inicio de sesión exitoso');

        // Redirección basada en rol
        if (data.role === 'manager') {
          navigate('/dashboard');
        } else {
          navigate('/developer-dashboard');
        }
      } else {
        toast.error('Respuesta inválida del servidor');
      }
    } catch (error: any) {
      console.error(error);
      toast.error(error.message || 'Error al iniciar sesión. Intenta de nuevo.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50 px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-slate-900 mb-2">Oracle Task Manager</h1>
          <p className="text-slate-600">Inicia sesión para gestionar tus proyectos</p>
        </div>
        
        <Card className="w-full shadow-lg">
          <CardHeader className="space-y-1">
            <CardTitle className="text-2xl text-center">Iniciar Sesión</CardTitle>
            <CardDescription className="text-center">
              Ingresa tus credenciales para acceder
            </CardDescription>
          </CardHeader>
          
          <form onSubmit={handleLogin}>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="email">Correo Electrónico</Label>
                <div className="relative">
                  <Mail className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                  <Input 
                    id="email"
                    type="email" 
                    placeholder="nombre@empresa.com"
                    className="pl-10"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
              </div>
              
              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <Label htmlFor="password">Contraseña</Label>
                  <a href="#" className="text-sm text-primary hover:underline">
                    ¿Olvidaste tu contraseña?
                  </a>
                </div>
                <div className="relative">
                  <LockKeyhole className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                  <Input 
                    id="password"
                    type="password" 
                    className="pl-10"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
              </div>
            </CardContent>
            
            <CardFooter className="flex flex-col gap-2">
              <Button 
                type="submit" 
                className="w-full bg-primary hover:bg-primary/90"
                disabled={isLoading}
              >
                {isLoading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
              </Button>
              <GoogleLoginButton />
              <RegisterButton 
                onClick={() => navigate('/register')}
                disabled={isLoading}
              />
            </CardFooter>
          </form>
        </Card>

        <div className="text-center mt-4 text-sm text-slate-500">
          &copy; {new Date().getFullYear()} Oracle Task Manager. Todos los derechos reservados.
        </div>
      </div>
    </div>
  );
};

export default Index;
