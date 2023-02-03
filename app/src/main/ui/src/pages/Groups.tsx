import {useEffect} from "react";

export default function Groups(props: any) {
  useEffect(() => {(document.title = props.title)}, [])
  return <>Groups</>
}
