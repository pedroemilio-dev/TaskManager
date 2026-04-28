import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { login } from "@/services/authService";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleLogin = async () => {
        try{
            const data = await login(email, password);
            console.log("resposta do backend:", data); 
            localStorage.setItem("token", data.accessToken);
            navigate("/inbox");
        } catch(err) {
            setError("Invalid email or password");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-[#0b0d11] p-4">
            <Card className="w-full max-w-md bg-[#13161b] border-[#21242a] shadow-2xl">
                <CardHeader className="pb-4">
                    <div className="flex items-center justify-center mb-1">
                        <CardTitle
                            className="text-2xl text-[#f3f5f9]" style={{ fontFamily: "Fraunces, Georgia, serif" }}>
                            Task Manager
                        </CardTitle>
                    </div>
                    <p className="text-center text-sm text-[#82868e]">
                        Welcome back. Pick up where you left off.
                    </p>
                </CardHeader>

                <CardContent className="flex flex-col gap-4">
                    <form onSubmit={(e) => { e.preventDefault(); handleLogin(); }} className="flex flex-col gap-4">
                        <div className="flex flex-col gap-2">
                            <Label className="text-[#ededf2]/85 text-xs font-medium">
                                Email
                            </Label>
                            <Input
                                className="h-10 bg-[#101215] border-[#2a2e34] text-white placeholder:text-[#54585f] focus-visible:border-[#619cfe] focus-visible:ring-[#619cfe]/30"
                                type="email" placeholder="you@example.com" value={email} onChange={(e) => setEmail(e.target.value)}/>
                        </div>

                        <div className="flex flex-col gap-2">
                            <Label className="text-[#ededf2]/85 text-xs font-medium flex justify-between items-center">
                                <span>Password</span>
                            </Label>
                            <Input
                                className="h-10 bg-[#101215] border-[#2a2e34] text-white placeholder:text-[#54585f] focus-visible:border-[#619cfe] focus-visible:ring-[#619cfe]/30"
                                type="password"  placeholder="••••••••" value={password}  onChange={(e) => setPassword(e.target.value)}
                            />
                        </div>

                        {error && (
                            <p className="text-sm text-[#fa6863]">{error}</p>
                        )}

                        <Button type="submit" className="h-11 w-full bg-[#619dff] text-[#0c121a] hover:bg-[#73b0ff] font-semibold">
                            Sign in →
                        </Button>
                    </form>

                    <p className="text-sm text-center text-[#81858d]">
                        Don't have an account?{" "}
                        <a href="/register" className="text-[#f3f5f9] border-b border-[#5c96f5] pb-px hover:text-[#5c96f5] font-medium no-underline">
                            Register
                        </a>
                    </p>
                </CardContent>
            </Card>
        </div>
    );
}