import React, {useContext, useState} from "react";
import {RadienContext} from "@/context/RadienContextProvider";
import useAssignableRoles from "@/hooks/useAssignableRoles";
import useAssignTenantRoles from "@/hooks/useAssignTenantRoles";
import useAssignedTenants from "@/hooks/useAssignedTenants";
import useNotifyUser from "@/hooks/useNotifyUser";
import {
    Box,
    Button, Container,
    Header,
    Multiselect, NonCancelableCustomEvent,
    Pagination, PaginationProps,
    SpaceBetween,
    Table,
    TableProps
} from "@cloudscape-design/components";
import {OptionDefinition} from "@cloudscape-design/components/internal/components/option/interfaces";
import PaginatedTable from "@/components/PaginatedTable/PaginatedTable";
import {QueryKeys} from "@/consts";
import usePaginatedUsers from "@/hooks/usePaginatedUsers";
import UserDetailsView from "@/components/UserDetailsView/UserDetailsView";
import {User} from "radien";
import {getColDefinitionUser} from "@/utils/tablesColDefinitions";
import {useRouter} from "next/router";
import {Loader} from "@/components/Loader/Loader";
import Form from "@cloudscape-design/components/form";

export default function () {
    const { i18n, addSuccessMessage, addErrorMessage, activeTenant: {data: tenantData} } = useContext(RadienContext);
    const { data: assignableRoles, isLoading: isLoadingRoles } = useAssignableRoles(tenantData?.tenantId!);
    const assignRole = useAssignTenantRoles();
    const notifyUser = useNotifyUser();
    const { locale } = useRouter();
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [pageSize, setPageSize] = useState<number>(10);
    const { isLoading, data } = usePaginatedUsers({ pageNo: currentPage, pageSize });
    const [loading, setLoading] = useState(false);

    const [targetRoles, setTargetRoles] = useState<OptionDefinition[]>([]);
    const [selectedItem, setSelectedItem] = useState<any>();
    const router = useRouter();
    const colDefinition: TableProps.ColumnDefinition<User>[] = getColDefinitionUser(i18n);

    const clickTargetUserPage = async (event: NonCancelableCustomEvent<PaginationProps.ChangeDetail>) => {
        let targetPage = event.detail.currentPageIndex;
        setCurrentPage(targetPage);
    };

    if (isLoadingRoles || isLoading) {
        return <Loader />;
    }

    const handleAssociateUser = async () => {
        if (!selectedItem) {
            addErrorMessage(`${i18n?.tenant_admin_associate_user_no_user_selected || "Please select a user to associate"}}`);
            return;
        }
        if (targetRoles.length === 0) {
            addErrorMessage(`${i18n?.tenant_admin_associate_user_no_role_selected || "Please select a role to associate"}}`);
            return;
        }

        setLoading(true);
        targetRoles.forEach((role) => {
            assignRole
                .mutateAsync({
                    userId: selectedItem.id,
                    tenantRoleId: Number(role.value),
                }).then((res) => {
                    if (res) {
                        addSuccessMessage(`${i18n?.tenant_admin_associate_user_success || "Successfully associated user with role"} ${role.label}`);
                        const viewId: string = "email-9";
                        const args = {
                            firstName: selectedItem.firstname,
                            lastName: selectedItem.lastname,
                            tenantName: tenantData?.tenantId,
                            roleList: targetRoles.map((t) => t.label).join(", "),
                        };

                        notifyUser.mutate({
                            email: selectedItem.email,
                            viewId,
                            language: locale,
                            params: args,
                        });
                    }
                }).catch((err) => {
                    addErrorMessage(err.message);
                }).finally(() => {
                    setLoading(false);
                });
            });

    }

    return  <Box padding="xl">
        <Container>
            {loading && <Loader />}

            <form
                onSubmit={(e) => {
                    e.preventDefault();
                    handleAssociateUser();
                }}
                className={"create-form--container"}>
                <Form
                    actions={
                        <SpaceBetween direction="horizontal" size="xs">
                            <Button formAction="none" variant="link" href={"/system/tenantAdmin"}>
                                {i18n?.tenant_admin_cancel_action || "Cancel"}
                            </Button>
                            <Button variant="primary">{i18n?.tenant_admin_associate_user_title || "Associate user"}</Button>
                        </SpaceBetween>
                    }
                    header={<Header variant="h1">{i18n?.tenant_admin_associate_user_title || "Associate user"}</Header>}>
                    <SpaceBetween  size={'xl'} direction={'vertical'}>
                        <Multiselect
                            selectedOptions={targetRoles}
                            onChange={({ detail }) => setTargetRoles(() => [...detail.selectedOptions])}
                            options={assignableRoles?.map((t) => {
                                return { label: t.role.name, value: String(t.tenantRole.id) };
                            })}
                            placeholder={i18n?.tenant_admin_select_user_roles || "Choose Role(s)"}
                        />
                        <Table
                            selectionType={"single"}
                            onSelectionChange={({ detail }) => setSelectedItem(detail.selectedItems[0])}
                            selectedItems={[selectedItem!]}
                            columnDefinitions={colDefinition}
                            items={data?.results!}
                            loading={isLoading}
                            loadingText={i18n?.tenant_admin_load_users_message || "Loading..."}
                            sortingDisabled
                            variant={"container"}
                            empty={
                                <Box textAlign="center" color="inherit">
                                    <b>{`${i18n?.tenant_admin_no_users_available || "No users available"}`}</b>
                                </Box>
                            }
                            header={<h4>Select user</h4>}
                            pagination={
                                <Pagination
                                    currentPageIndex={data?.currentPage!}
                                    pagesCount={data?.totalPages!}
                                    onChange={(event) => clickTargetUserPage(event)}
                                    ariaLabels={{
                                        nextPageLabel: `${i18n?.pagination_next_page || "Next page"}`,
                                        previousPageLabel: `${i18n?.pagination_previous_page || "Previous page"}`,
                                        pageLabel: (pageNumber) => `Page ${pageNumber} of all pages`,
                                    }}
                                />
                            }
                        />
                    </SpaceBetween>
                </Form>
            </form>
        </Container>
    </Box>
}