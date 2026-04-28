import {
    Dialog, DialogContent, DialogHeader, DialogTitle,
} from "@/components/ui/dialog"
import {
    DropdownMenu, DropdownMenuContent, DropdownMenuGroup,
    DropdownMenuItem, DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { Calendar } from "@/components/ui/calendar"
import { Trash, Flag, Calendar as CalendarIcon, Hash } from "lucide-react"

interface EditTaskDialogProps {
    open: boolean;
    onOpenChange: (isOpen: boolean) => void;
    name: string;
    onNameChange: (value: string) => void;
    description: string;
    onDescriptionChange: (value: string) => void;
    priority: string;
    onPriorityChange: (value: string) => void;
    dueDate: Date | undefined;
    onDueDateChange: (date: Date | undefined) => void;
    onSubmit: () => void;
    onDelete: () => void;
}

export default function EditTaskDialog({
    open,
    onOpenChange,
    name,
    onNameChange,
    description,
    onDescriptionChange,
    priority,
    onPriorityChange,
    dueDate,
    onDueDateChange,
    onSubmit,
    onDelete,
}: EditTaskDialogProps) {
    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="
                bg-[#13161B] !max-w-2xl w-full min-h-[400px] px-0
                [&>button.absolute]:bg-transparent [&>button.absolute]:text-[#3f3f46] [&>button.absolute]:hover:text-white
                [&>button.absolute]:rounded-lg [&>button.absolute]:p-2 [&>button.absolute]:hover:bg-[#52525b]">

                <DialogHeader className="border-b border-[#424242] px-8">
                    <DialogTitle className="text-white text-2xl mb-3">Edit Task</DialogTitle>
                </DialogHeader>

                <div className="grid grid-cols-5 gap-6 px-8 border-b border-[#424242]">
                    <div className="col-span-5 flex flex-col gap-4 border-b border-dashed border-[#424242]">
                        <div className="flex flex-col gap-1">
                            <Textarea
                                className="bg-transparent border-none shadow-none text-white !text-2xl font-light placeholder:text-[#71717a] focus-visible:ring-0 px-0"
                                value={name}
                                onChange={(e) => onNameChange(e.target.value)}
                            />
                        </div>

                        <Textarea
                            className="w-full min-h-[120px] max-h-[200px] overflow-y-auto px-0 py-3 bg-transparent border-none text-white placeholder:text-[#71717a] focus-visible:ring-0 resize-none"
                            placeholder="Add a description, links, or context..."
                            value={description}
                            onChange={(e) => onDescriptionChange(e.target.value)}
                        />
                    </div>

                    <div></div>

                    <div className="mb-4 flex flex-col items-center gap-2">
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="outline" className="w-full bg-transparent border-dashed rounded-full text-[#71717a] border-[#3f3f46]">
                                    {priority !== "DEFAULT" ? priority : <div className="flex items-center gap-1"><Flag /> Priority</div>}
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent className="bg-[#13161B] border-[#3f3f46] text-white">
                                <DropdownMenuGroup>
                                    <DropdownMenuItem onClick={() => onPriorityChange("LOW")} className="hover:bg-[#1f232a]! hover:text-white!">Low</DropdownMenuItem>
                                    <DropdownMenuItem onClick={() => onPriorityChange("MEDIUM")} className="hover:bg-[#1f232a]! hover:text-white!">Medium</DropdownMenuItem>
                                    <DropdownMenuItem onClick={() => onPriorityChange("HIGH")} className="hover:bg-[#1f232a]! hover:text-white!">High</DropdownMenuItem>
                                </DropdownMenuGroup>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>

                    <div className="flex flex-col items-center gap-2">
                        <Popover>
                            <PopoverTrigger asChild>
                                <Button variant="outline" className="w-full justify-center bg-transparent border-dashed rounded-full text-[#71717a] border-[#3f3f46]">
                                    {dueDate ? dueDate.toLocaleDateString("pt-PT") : <div className="flex items-center gap-1"><CalendarIcon /> Due Date</div>}
                                </Button>
                            </PopoverTrigger>
                            <PopoverContent className="w-auto p-0" align="start">
                                <Calendar mode="single" selected={dueDate} onSelect={onDueDateChange} />
                            </PopoverContent>
                        </Popover>
                    </div>

                    <div className="flex flex-col items-center gap-1">
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="outline" className="w-full bg-transparent border-dashed rounded-full text-[#71717a] border-[#3f3f46]">
                                    <Hash /> Project
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent className="bg-[#13161B] border-[#3f3f46] text-white">
                                <DropdownMenuGroup>
                                    <DropdownMenuItem className="hover:bg-[#1f232a]! hover:text-white!">Project A</DropdownMenuItem>
                                </DropdownMenuGroup>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>
                </div>

                <div className="flex justify-between px-8">
                    <Button onClick={onDelete} className="rounded-sm text-[#FB7185] border-[#3f3f46] hover:text-[#FB7185] hover:bg-[#2A1A22]" variant="ghost">
                        <Trash size={18} />
                        Delete
                    </Button>

                    <div className="flex gap-2">
                        <Button onClick={() => onOpenChange(false)} variant="ghost" className="rounded-sm text-[#71717a] hover:text-white hover:bg-[#27272a]">
                            Cancel
                        </Button>
                        <Button onClick={onSubmit} className="rounded-sm bg-[#619dff]! hover:bg-[#73b0ff]! text-[#0c121a] font-semibold">
                            Save Changes
                        </Button>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    );
}