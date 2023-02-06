import {useEffect} from "react";

export default function Users(props: {title: string}) {
  useEffect(() => {(document.title = props.title)}, [])
  return <>Users</>
}
