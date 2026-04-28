import axios from "axios";
import { LucidePhoneCall } from "lucide-react";

const API_URL = "http://localhost:8080/api/tasks";

export const getInboxTasks = async () => {
    const token = localStorage.getItem("token");

    const response = await axios.get(`${API_URL}/inbox`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    return response.data;
};

export const createTask = async (name: string, description?: string, dueDate?: string | null, priority?: string, projectId?: number | null) => {
    const token = localStorage.getItem("token");

    const response = await axios.post(API_URL, {name, description, dueDate, priority, projectId}, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    return response.data;
};

export const deleteTask = async (taskId: number) => {
    const token = localStorage.getItem("token");

    const response = await axios.delete(`${API_URL}/${taskId}`, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });

    return response.data;
}

export const editTask = async (taskId: number, name?: string, description?: string, dueDate?: string | null, priority?: string, projectId?: number | null) => {
    const token = localStorage.getItem("token");

    const response = await axios.patch(`${API_URL}/${taskId}`, {name, description, dueDate, priority, projectId}, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
    
    return response.data;
}