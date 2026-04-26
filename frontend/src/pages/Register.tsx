import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { register } from "@/services/authService";

export default function Register() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const validateEmail = (email: string) => {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    const isStrongPassword = (password: string) => {
        return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/.test(password);
    }

    const handleRegister = async () => {
        if(!validateEmail) {
            setError("Please enter a valid email address!");
        }
        if(!isStrongPassword) {
            setError("Please enter a stronger password!");
        }

        try{
            const data = await register(name, email, password);
            localStorage.setItem("token", data.token);
            navigate("/inbox");
        } catch(err) {
            setError("Invalid email or password");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-[#0b0d11] p-4">
            <Card className="w-full max-w-md bg-[#13161b] border-[#21242a] shadow-2xl">
                <CardHeader className="pb-4">
                    <div className="flex items-center justify-center gap-2.5 mb-1">
                        <CardTitle
                            className="text-2xl text-[#f3f5f9]" style={{ fontFamily: "Fraunces, Georgia, serif" }}>
                            Create an account
                        </CardTitle>
                    </div>
                    <CardDescription className="text-center text-sm text-[#82868e]">
                        Enter your details to get started.
                    </CardDescription>
                </CardHeader>

                <CardContent className="flex flex-col gap-4">
                    <form onSubmit={(e) => { e.preventDefault(); handleRegister(); }} className="flex flex-col gap-4">
                        <div className="flex flex-col gap-2">
                            <Label className="text-[#ededf2]/85 text-xs font-medium">
                                Name
                            </Label>
                            <Input
                                className="h-10 bg-[#101215] border-[#2a2e34] text-white placeholder:text-[#54585f] focus-visible:border-[#619cfe] focus-visible:ring-[#619cfe]/30"
                                type="text" placeholder="Jane Doe" value={name} onChange={(e) => setName(e.target.value)}/>
                        </div>

                        <div className="flex flex-col gap-2">
                            <Label className="text-[#ededf2]/85 text-xs font-medium">
                                Email
                            </Label>
                            <Input
                                className="h-10 bg-[#101215] border-[#2a2e34] text-white placeholder:text-[#54585f] focus-visible:border-[#619cfe] focus-visible:ring-[#619cfe]/30"
                                type="email" placeholder="you@example.com" value={email} onChange={(e) => setEmail(e.target.value)}/>
                        </div>

                        <div className="flex flex-col gap-2">
                            <Label className="text-[#ededf2]/85 text-xs font-medium">
                                Password
                            </Label>
                            <Input
                                className="h-10 bg-[#101215] border-[#2a2e34] text-white placeholder:text-[#54585f] focus-visible:border-[#619cfe] focus-visible:ring-[#619cfe]/30"
                                type="password" placeholder="At least 8 characters" value={password} onChange={(e) => setPassword(e.target.value)}/>
                            <div className="flex gap-1 mt-1">
                                {[1, 2, 3, 4].map((i) => ( <span key={i}/> ))}
                            </div>
                        </div>

                        {error && (
                            <p className="text-sm text-[#fa6863]">{error}</p>
                        )}

                        <Button
                            type="submit"
                            className="h-11 w-full bg-[#619dff] text-[#0c121a] hover:bg-[#73b0ff] font-semibold">
                            Create account →
                        </Button>
                    </form>

                    <p className="text-sm text-center text-[#81858d]">
                        Already have an account?{" "}
                        <a href="/login" className="text-[#f3f5f9] border-b border-[#5c96f5] pb-px hover:text-[#5c96f5] font-medium no-underline">
                            Login
                        </a>
                    </p>
                </CardContent>
            </Card>
        </div>
    );
}