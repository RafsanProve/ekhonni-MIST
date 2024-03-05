"use client"

import {useSession} from "next-auth/react";
import {useRouter} from "next/navigation";
import {FallingLines} from "react-loader-spinner";

const RedirectPage = () => {

    const router = useRouter();
    const {data: session} = useSession();
    setTimeout(async () => {

        if (session?.user.user.role === "ROLE_ADMIN") {
            router.push("/admin-page")
        } else {
            router.push("/")
        }
    } , 1000)
    return (
        <div className="absolute inset-0 z-10 w-full h-full flex flex-col justify-center items-center bg-neutral-800">
            <h1 className="text-xl text-white mb-3">Redirecting...</h1>
            <FallingLines color="#ffffff" width="100" visible={true} ariaLabel="falling-circles-loading"/>
        </div>
    )
}

export default RedirectPage;