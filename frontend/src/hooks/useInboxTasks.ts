import { useEffect, useState } from "react";
import { getInboxTasks, createTask, editTask, deleteTask } from "@/services/taskService";

export function useInboxTasks() {

    const [tasks, setTasks] = useState([]);

    // ─── Task Creation ───────────────────────────────────────────────
    const [createOpen, setCreateOpen] = useState(false);
    const [name, setName] = useState("");
    const [nameError, setNameError] = useState("");
    const [description, setDescription] = useState("");
    const [dueDate, setDueDate] = useState<Date | undefined>(undefined);
    const [priority, setPriority] = useState("DEFAULT");

    // ─── Task Edit ───────────────────────────────────────────────
    const [editOpen, setEditOpen] = useState(false);
    const [selectedTask, setSelectedTask] = useState<any | null>(null);
    const [editName, setEditName] = useState("");
    const [editDescription, setEditDescription] = useState("");
    const [editPriority, setEditPriority] = useState("DEFAULT");
    const [editDueDate, setEditDueDate] = useState<Date | undefined>(undefined);

    useEffect(() => {
        fetchTasks();
    }, []);

    const fetchTasks = async () => {
        try {
            const data = await getInboxTasks();
            setTasks(data);
        } catch (error) {
            console.log("Error fetching tasks:", error);
        }
    };

    const handleNameChange = (value: string) => {
        setName(value);
        if (nameError) setNameError("");
    };

    const handleCreateOpenChange = (isOpen: boolean) => {
        setCreateOpen(isOpen);
        if (!isOpen) resetCreateForm();
    };

    const handleCreateTask = async () => {
        if (!name.trim()) {
            setNameError("Task name cannot be empty");
            return;
        }
        try {
            const formattedDueDate = dueDate
                ? `${dueDate.getFullYear()}-${String(dueDate.getMonth() + 1).padStart(2, "0")}-${String(dueDate.getDate()).padStart(2, "0")}`
                : null;
            console.log("dueDate enviada:", formattedDueDate);

            await createTask(name, description, formattedDueDate, priority, null);
            resetCreateForm();
            setCreateOpen(false);
            await fetchTasks();
        } catch (error) {
            console.log("Error creating task:", error);
        }
    };

    const handleEditTask = async () => {
        try{
            const formattedDueDate = editDueDate
                ? `${editDueDate.getFullYear()}-${String(editDueDate.getMonth() + 1).padStart(2, "0")}-${String(editDueDate.getDate()).padStart(2, "0")}`
                : null;

            await editTask(selectedTask.id, editName, editDescription, formattedDueDate, editPriority, null);

            setEditOpen(false);
            await fetchTasks();
        } catch (error) {
            console.log("Error editing task: ", error);
        }
    }

    const resetCreateForm = () => {
        setName("");
        setNameError("");
        setDescription("");
        setDueDate(undefined);
        setPriority("DEFAULT");
    };

    const handleOpenTask = (task: any) => {
        setSelectedTask(task);
        setEditName(task.name || "");
        setEditDescription(task.description || "");
        setEditPriority(task.priority || "DEFAULT");
        setEditDueDate(task.dueDate ? new Date(task.dueDate + "T00:00:00") : undefined);
        setEditOpen(true);
    };

    const handleDeleteTask = async () => {
        try {
            await deleteTask(selectedTask.id);
            setEditOpen(false);
            await fetchTasks();
        } catch (error) {
            console.log("Error deleting task:", error);
        }
    };

    return {
        tasks,
        onTaskClick: handleOpenTask,
        createDialog: {
            open: createOpen,
            onOpenChange: handleCreateOpenChange,
            name,
            onNameChange: handleNameChange,
            nameError,
            description,
            onDescriptionChange: setDescription,
            dueDate,
            onDueDateChange: setDueDate,
            priority,
            onPriorityChange: setPriority,
            onSubmit: handleCreateTask,
        },
        editDialog: {
            open: editOpen,
            onOpenChange: setEditOpen,
            name: editName,
            onNameChange: setEditName,
            description: editDescription,
            onDescriptionChange: setEditDescription,
            priority: editPriority,
            onPriorityChange: setEditPriority,
            dueDate: editDueDate,
            onDueDateChange: setEditDueDate,
            onDelete: handleDeleteTask,
            onSubmit: handleEditTask,
        }
    };
}