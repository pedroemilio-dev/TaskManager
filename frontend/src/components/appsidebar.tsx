import { useNavigate, useLocation } from "react-router-dom"
import { Inbox, Calendar, AlertCircle, CheckCircle, Plus  } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  Sidebar, SidebarContent, SidebarFooter, SidebarGroup, SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader, SidebarMenu, SidebarMenuButton, SidebarMenuItem,
  SidebarTrigger, useSidebar,
} from "@/components/ui/sidebar"

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"

import {
  InputGroup,
  InputGroupAddon,
  InputGroupButton,
  InputGroupInput,
  InputGroupText,
  InputGroupTextarea,
} from "@/components/ui/input-group"
import { Label } from "./ui/label"
import { Input } from "./ui/input"
import { useState } from "react"
import { logout } from "@/services/authService";

const navItems = [
  { label: "Inbox",        path: "/inbox",      icon: Inbox       },
  { label: "Esta semana", path: "/semana",    icon: Calendar    },
  { label: "Urgentes",    path: "/urgentes",  icon: AlertCircle },
  { label: "Concluídas",  path: "/concluidas",icon: CheckCircle },
]

export function AppSidebar() {
  const navigate = useNavigate()
  const location = useLocation()
  const { state } = useSidebar()
  const [name, setName] = useState("");
  const [color, setColor] = useState("");
  const [parentProject, setParentProject] = useState("");
  const [error, setError] = useState("");

  const handleLogout = async () => {
    try{
        await logout();
        navigate("/login");
    } catch(err) {
        setError("No user to log out");
    }
  }

  return (
    <Sidebar collapsible="icon" className="bg-[#0B0D11] border-b border-[#424242]">
      <SidebarHeader className="flex flex-row items-center justify-between px-3 py-2 border-b border-[#424242]">
        {state === "expanded" && (
          <span className="font-medium">⬡ Tasks</span>
        )}
        <SidebarTrigger />
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              {navItems.map((item) => (
                <SidebarMenuItem key={item.path}>
                  <SidebarMenuButton tooltip={item.label} isActive={location.pathname === item.path} onClick={() => navigate(item.path)}>
                    <item.icon />
                    <span>{item.label}</span>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <SidebarGroup>
                <SidebarGroupLabel>Projects</SidebarGroupLabel>

                <SidebarMenu>
                    <SidebarMenuItem>
                        <Dialog>
                            <DialogTrigger asChild>
                                <SidebarMenuButton>
                                    <Plus />
                                    <span>New Project</span>
                                </SidebarMenuButton>
                            </DialogTrigger>

                            <DialogContent>
                                <DialogHeader>
                                    <DialogTitle>Create New Project</DialogTitle>
                                </DialogHeader>
                                
                                <div className="flex flex-col gap-1">
                                    <Label>Project Name</Label>
                                    <Input
                                        type="name"
                                        placeholder=""
                                        value={name}
                                        onChange={(e) => setName(e.target.value)}/>
                                </div>

                                <div className="flex flex-col gap-1">
                                    <Label>Color</Label>
                                    <Input
                                        type="name"
                                        placeholder=""
                                        value={color}
                                        onChange={(e) => setColor(e.target.value)}/>
                                </div>

                                <div className="flex flex-col gap-1">
                                    <Label>Parent Project</Label>
                                    <Input
                                        type="name"
                                        placeholder=""
                                        value={parentProject}
                                        onChange={(e) => setParentProject(e.target.value)}/>
                                </div>

                                <Button  className="w-full mt-2 bg-[#3f3f46] text-white hover:bg-[#52525b]">
                                  Create Project
                                </Button>
                            </DialogContent>
                        </Dialog>
                    </SidebarMenuItem>
                </SidebarMenu>
            </SidebarGroup>
      </SidebarContent>

      <SidebarFooter>
        <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                      <Button variant="outline">Open</Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent>
                      <DropdownMenuGroup>
                          <DropdownMenuLabel>My Account</DropdownMenuLabel>
                          <DropdownMenuItem>Settings</DropdownMenuItem>
                      </DropdownMenuGroup>
                      <DropdownMenuSeparator />
                      <DropdownMenuGroup>
                          <DropdownMenuItem onClick={handleLogout}>Log out</DropdownMenuItem>
                      </DropdownMenuGroup>
                  </DropdownMenuContent>
              </DropdownMenu>
      </SidebarFooter>
    </Sidebar>
  )
}