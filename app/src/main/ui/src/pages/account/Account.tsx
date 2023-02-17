import {useValidationSchema} from "../../hooks/useValidationSchema";
import {useAccount} from "../../hooks/useAccount";
import AccountForm from "./AccountForm";
import {useEffect} from "react";
import {LoadingIcon} from "@cloudogu/ces-theme-tailwind";

export default function Account(props: { title: string }) {
  const validationSchema = useValidationSchema()
  const {account, isLoading, setAccount} = useAccount();

  useEffect(() => {
    (document.title = props.title)
  }, []);

  if (isLoading) {
    return (
      <div className={"flex row justify-center w-[100%] mt-16"}>
        <LoadingIcon className={"w-64 h-64"}/>
      </div>
    )
  }
  return (
    <AccountForm account={account} setAccount={setAccount} validationSchema={validationSchema}></AccountForm>
  )
}
