import {TableProps} from "@cloudscape-design/components";
import {User} from "radien";

export const getColDefinitionUser: (i18n: any) =>  TableProps.ColumnDefinition<User>[] = (i18n) => [
    {
        id: "logon",
        header: i18n?.user_management_column_username || "Username",
        cell: (item: User) => item?.logon || "-",
        sortingField: "logon",
    },
    {
        id: "firstname",
        header: i18n?.user_management_column_firstname || "First Name",
        cell: (item: User) => item?.firstname || "-",
        sortingField: "firstname",
    },
    {
        id: "lastname",
        header: i18n?.user_management_column_lastname || "Last Name",
        cell: (item: User) => item?.lastname || "-",
        sortingField: "lastname",
    },
    {
        id: "userEmail",
        header: i18n?.user_management_column_email || "User Email",
        cell: (item: User) => item?.userEmail || "-",
        sortingField: "userEmail",
    },
];
