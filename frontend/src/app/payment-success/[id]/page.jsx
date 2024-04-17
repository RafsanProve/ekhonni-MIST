"use client"


import Header from "@/components/Header";
import Button from "@/components/Button";
import useSWRMutation from "swr/mutation";
import {baseUrl} from "@/utils/baseUrl";
import {postCallFetcher} from "@/utils/fetcher";
import {toast, Toaster} from "sonner";
import {useRouter} from "next/navigation";
import successIcon from "../../../../public/tick_icon.svg"
import Image from "next/image";

const SslSuccess = ({params}) => {

    const router = useRouter();
    const tranId = params.id;
    const url = "/user/validate";
    const method = "POST";
    const headers = {'Content-Type': 'application/json'}

    const {data: response, trigger} = useSWRMutation([baseUrl, url, method, headers], postCallFetcher)
    return (
        <>
            <Header/>
            <Toaster richColors position={"top-right"}/>
            <div className="flex flex-col border-2 justify-center items-center text-center">
                <Image className="mt-10" src={successIcon} alt="success" width={100} height={100}/>
                <h1 className=" text-5xl font-bold margin mb-3 mt-5">THANK YOU!</h1>
                <div className="mt-3">
                    <h3 className="mb-1">The payment is successful</h3>
                    <div className="">
                        <div className="">Your transaction id is</div>
                        <div className="font-bold"> {tranId}</div>

                    </div>
                </div>
                <div className="mt-10 mb-10">
                    <Button value={"Done"} option={1} type={"button"}
                            onClick={async () => {
                                const value = {"tran_id": tranId, "transactionStatus": "SUCCESS"}
                                try {
                                    const {data: {transactionStatus}} = await trigger(value);
                                    if (transactionStatus.toUpperCase() === "SUCCESS".toUpperCase()) {
                                        await router.replace("/");
                                    }

                                } catch (e) {
                                    toast.error(e.message)
                                }

                            }}/>
                </div>
            </div>
        </>
    );
}

export default SslSuccess;