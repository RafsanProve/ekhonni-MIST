import NotificationListItem from "@/components/NotificationListItem";
import {requestApi} from "@/utils/axios.settings";
import {toast, Toaster} from "sonner";
import {useSWRConfig} from "swr";

const NotificationListModal = ({setModalOpen, notificationList}) => {

    const {mutate} = useSWRConfig();
    const handleClearAll = async () => {
        const req = {"Content-Type": "application/json"}
        const url = "/user/notification/delete/all"
        const method = "DELETE"
        await requestApi({req, url, method})
        toast.success("All notifications cleared")
        mutate("/user/notification/fetch");
    }
    return (
        <div id="notificationModal"
             className="flex flex-col items-end justify-start w-fit h-auto max-h-72 absolute z-10 top-16 right-20 m-3">
            <Toaster richColors position={"top-right"}/>
            {notificationList.length !== 0 &&
                (<>
                    <ul id="notificationList"
                        className="bg-white list-none overflow-hidden hover:overflow-y-scroll border border-neutral-300 flex flex-col justify-start items-start rounded-lg shadow-md shadow-slate-400">
                        {notificationList.map((notification) => (
                            <NotificationListItem key={notification.id} message={notification.text}
                                                  link={notification.link}
                                                  time={notification.formattedTime}/>
                        ))}
                    </ul>
                    <button
                        className="text-black px-2 py-0.5 mt-1 mr-1 border border-neutral-300 shadow-md shadow-slate-400 text-md bg-white rounded-full  transition duration-500 hover:scale-[103%] hover:-translate-y-1 active:translate-y-2"
                        onClick={() => {
                            handleClearAll()
                        }}
                    >Clear All
                    </button>
                </>)
            }
            {notificationList.length === 0 &&
                (<div
                    className="w-60 h-12 mr-28 cursor-default flex justify-center items-center bg-white border border-neutral-300 shadow-md shadow-slate-300 rounded-lg transition ease-in-out duration-500 hover:scale-y-[102%] hover:-translate-y-1">
                    <p className="font-medium">No Notifications</p>
                </div>)
            }
        </div>
    )
}

export default NotificationListModal;