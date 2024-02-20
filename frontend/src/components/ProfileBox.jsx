import Image from "next/image";
import Button from "@/components/Button";
import ProfileDiv from "@/components/ProfileDiv";

const ProfileBox = ()=>{
    return (
        <>
            <div className="w-[300px] top-16 absolute z-10 bg-white border-2 border-black overflow-hidden float-right right-1 rounded-lg m-3">
                <div className="w-full h-[110px] flex border-b-2 border-black">
                    <div className="w-1/3 h-full relative">
                        <Image src={"/bike.jpg"} alt={"Profile"} fill objectFit={"cover"} className={"rounded-full px-1 py-2"}/>
                    </div>
                    <div className={"w-2/3 h-full flex justify-start items-center pl-4"}>
                        <h1 className={"tracking-widest text-xl font-semibold"}>Shahabuddin akhon</h1>
                    </div>
                </div>
                <div className="w-full h-[80px] py-3 px-2 gap-x-3 flex justify-center items-center">
                    <Button value={"Your Bid"} option={true} type={"submit"}/>
                    <Button value={"Your Product"} option={true} type={"submit"}/>
                </div>
                <div className={"w-full mt-3"}>
                    <ProfileDiv image={"/edit.svg"} text={"Edit Profile"} color={"bg-slate-100"}/>
                    <ProfileDiv image={"/logout.svg"} text={"Logout"} color={"bg-slate-100"}/>
                    <ProfileDiv image={"/delete.svg"} text={"Delete Account"} color={"bg-red-500"}/>
                </div>
            </div>
        </>
    )
}
export default ProfileBox;