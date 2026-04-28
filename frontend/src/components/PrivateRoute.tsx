import { Navigate, Outlet } from "react-router-dom"

function isTokenValid(token: string | null): boolean {
    if (!token) return false

    try {
        const payload = JSON.parse(atob(token.split(".")[1]))
        const currentTime = Math.floor(Date.now() / 1000)

        return payload.exp && payload.exp > currentTime
    } catch (error) {
        return false
    }
}

export default function PrivateRoute() {
    const token = localStorage.getItem("token");

    console.log("TOKEN FRONTEND:", token);
    return isTokenValid(token) ? <Outlet /> : <Navigate to="/login" replace />
}