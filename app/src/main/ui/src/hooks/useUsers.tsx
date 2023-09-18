import {UsersService} from "../services/Users";
import {usePaginatedData} from "./usePaginatedData";
export default function useUsers() {
    return usePaginatedData(UsersService.find, {start: 0, pageSize: 20});
}
