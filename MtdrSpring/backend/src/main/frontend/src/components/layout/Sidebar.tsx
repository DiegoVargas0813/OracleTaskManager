
import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { 
  Layout, 
  LayoutDashboard, 
  Users, 
  Calendar, 
  Settings, 
  ChevronLeft, 
  ChevronRight,
  FolderKanban,
  BarChart3,
  CalendarClock
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { useCurrentUser } from '@/hooks/useCurrentUser';
import { useProjects } from '@/hooks/useProjects';

const Sidebar = () => {
  const [collapsed, setCollapsed] = useState(false);
  const { data: currentUser } = useCurrentUser();
  const { data: projects } = useProjects(currentUser?.id);
  
  const defaultProjectId = projects && projects.length > 0 ? projects[0].id : 1;

  const toggleSidebar = () => {
    setCollapsed(!collapsed);
  };

  // Define menu items based on user role
  const getMenuItems = () => {
    if (currentUser?.role === "manager") {
      return [
        { name: 'Dashboard', icon: LayoutDashboard, path: '/dashboard' },
      { name: 'Your Project', icon: FolderKanban, path: `/projects/${defaultProjectId}` },
      { name: 'Reports', icon: BarChart3, path: '/reports' },
      { name: 'Settings', icon: Settings, path: '/settings' },
      ];
    }

    // Default menu items for managers and other roles
    return [
      { name: 'Dashboard', icon: LayoutDashboard, path: '/developer-dashboard' },
        { name: 'Your Project', icon: FolderKanban, path: `/developer-project-details/${defaultProjectId}` },


    ];
  };

  const menu = getMenuItems();

  return (
    <aside 
      className={cn(
        "bg-sidebar h-screen transition-all duration-300 border-r border-border flex flex-col",
        collapsed ? "w-[70px]" : "w-[240px]"
      )}
    >
      <div className="flex items-center p-4 border-b border-border">
        <Layout className="h-6 w-6 text-primary mr-2" />
        {!collapsed && <span className="font-semibold text-lg animate-fade-in">MetriK</span>}
      </div>
      
      <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
        {menu.map((item) => (
          <NavLink
            key={item.name}
            to={item.path}
            className={({ isActive }) => cn(
              "sidebar-link",
              isActive ? "active" : "",
              "group"
            )}
          >
            <item.icon size={20} />
            {!collapsed && (
              <span className="animate-slide-in">{item.name}</span>
            )}
          </NavLink>
        ))}
      </nav>

      <div className="p-4 border-t border-border">
        <button 
          onClick={toggleSidebar}
          className="w-full flex items-center justify-center p-2 rounded-md hover:bg-sidebar-accent transition-colors"
        >
          {collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
          {!collapsed && <span className="ml-2">Collapse</span>}
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
