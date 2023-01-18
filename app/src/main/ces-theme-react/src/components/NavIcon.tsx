import {ArrowRightOnRectangleIcon} from "@heroicons/react/24/solid";
import {UserGroupIcon, UserIcon, UsersIcon} from "@heroicons/react/24/outline";
import {cl} from "dynamic-class-list";

export type NavIconType = "users" | "groups" | "user" | "logout"

export type NavIconProps = {
  type: NavIconType;
  className?: string;
};
export default function NavIcon({type, className}: NavIconProps) {
  if (type == null){
    return <></>
  }

  const classes = cl("w-5 h-5 mr-2", className);

  switch (type) {
    case "users":
      return <UsersIcon className={classes}/>
    case "groups":
      return <UserGroupIcon className={classes}/>
    case "user":
      return <UserIcon className={classes}/>;
    case "logout":
      return <ArrowRightOnRectangleIcon className={classes}/>;
  }
  return <>
  </>
}
