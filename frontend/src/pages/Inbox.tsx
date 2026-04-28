import { useInboxTasks } from "@/hooks/useInboxTasks";
import CreateTaskDialog from "@/components/inbox/CreateTaskDialog";
import EditTaskDialog from "@/components/inbox/EditTaskDialog";

export default function Inbox() {
    const { tasks, onTaskClick, createDialog, editDialog } = useInboxTasks();

    return (
        <div className="min-h-screen bg-[#0b0d11] p-4">
            <div className="flex items-center justify-between pt-6">
                <h1 className="text-white text-3xl">Inbox</h1>
                <CreateTaskDialog {...createDialog} />
            </div>

            <div className="mt-6 px-8">
                {tasks.map((task: any) => (
                    <button
                        key={task.id}
                        onClick={() => onTaskClick(task)}
                        className="border-[#424242] px-8 w-full p-2 border-b text-white text-left">
                        {task.name}
                    </button>
                ))}
            </div>

            <EditTaskDialog {...editDialog} />
        </div>
    );
}