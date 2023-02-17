import {useEffect} from "react";

export default function Groups(props: {title: string}) {
  useEffect(() => {(document.title = props.title)}, [])
  return <>Groups</>
}
