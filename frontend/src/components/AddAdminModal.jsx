"use client"
import {useState} from 'react';
import TextField from "@/components/TextField";
import Button from "@/components/Button";

const AddAdminModal = ({CloseModel}) => {
    const [closeModel, setCloseModel] = useState(false);
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const CloseButton = () => {
        setCloseModel(false);
        CloseModel(false);
    }
    function handleSubmit(event) {
        event.preventDefault();
        const formDataObject = {
            email: email,
            password: password
        };

    }
    return (
        <>
            <form onSubmit={handleSubmit}>
                <div className=" z-20  absolute inset-0 flex justify-center items-center  bg-opacity-20 backdrop-blur-[1px] flex-col">
                    <div className="w-[450px] h-[2px] left-0 bg-transparent z-10 flex justify-end items-center">
                        <button onClick={CloseButton}><p className="mb-4 mr-1 text-black">X</p></button>
                    </div>
                    <div
                        className="w-[450px] h-[375px]  left-0 border-neutral-400 bg-slate-100 rounded-lg  flex  flex-col justify-center  items-center">

                        <div
                            className="w-full h-full   flex flex-col justify-center items-center rounded-lg   shadow-md shadow-slate-500 ">
                            <div className="w-10/12 h-1/5  flex flex-col justify-center items-center mb-4 ">
                                <p className="text-3xl font-medium my-3">New Admin Info</p>
                                <p className="text-lg"> Enter Email and Password</p>
                            </div>
                            <div className=" w-10/12 h-2/5 flex  flex-col justify-center items-center ">
                                <TextField placeholder={"Email"} type={"text"}
                                           name={"email"} value={email}
                                           onChange={(e) => {
                                               setEmail(e.target.value)
                                           }}
                                />
                                <TextField placeholder={"Password"} type={"password"} name={"password"}
                                           value={password}
                                           onChange={(e) => {
                                               setPassword(e.target.value)
                                           }}
                                />
                            </div>
                            <div className=" w-10/12  h-1/5 flex flex-col justify-center items-end mr-6">
                                <Button onClick={CloseButton} value={"Add Admin"} option={1} type={"submit"}/>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </>

    );
}
export default AddAdminModal