import Link from "next/link";


const NotificationListItem = ({message, link, time}) => {
    return (
        <li className="w-80 min-h-35 mb-2 p-1 bg-white border border-neutral-300 shadow-md shadow-slate-300 rounded-lg transition ease-in-out duration-500 hover:scale-y-[102%] hover:-translate-y-1">
            <Link href={link?link:"#"}>
                <div className="w-full h-1/2 pl-2 flex items-center">
                    <p className="font-normal text-neutral-800 text-base"><span
                        className="bg-gradient-radial from-slate-400 to-slate-950 rounded-full px-[6px] text-xs text-slate-500">.</span>&nbsp;{message}
                    </p>
                </div>
                <div className="w-full h-1/2 pl-7 flex items-center">
                    <p className="font-medium text-black opacity-50 text-sm">{time}</p>
                </div>
            </Link>
        </li>
    )
}

export default NotificationListItem;