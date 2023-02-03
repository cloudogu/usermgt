import {useEffect} from "react";

export default function Users(props: any) {
  useEffect(() => {(document.title = props.title)}, [])
  return <>Users</>
}
