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
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [pageSize, setPageSize] = useState<number>(10);
    const { isLoading, data, refetch } = usePaginatedUsers({ pageNo: currentPage, pageSize });
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
            addErrorMessage("Please select a user to associate");
            return;
        }
        if (targetRoles.length === 0) {
            addErrorMessage("Please select a role to associate");
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
                        addSuccessMessage(`Successfully associated user with role ${role.label}`);
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
                            language: "en",
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
                                {i18n?.create_user_cancel_action || "Cancel"}
                            </Button>
                            <Button variant="primary">Associate user</Button>
                        </SpaceBetween>
                    }
                    header={<Header variant="h1">Associate user</Header>}>
                    <SpaceBetween  size={'xl'} direction={'vertical'}>
                        <Multiselect
                            selectedOptions={targetRoles}
                            onChange={({ detail }) => setTargetRoles(() => [...detail.selectedOptions])}
                            options={assignableRoles?.map((t) => {
                                return { label: t.role.name, value: String(t.tenantRole.id) };
                            })}
                            placeholder={i18n?.user_management_tenant_request_choose_roles || "Choose Role(s)"}
                        />
                        <Table
                            selectionType={"single"}
                            onSelectionChange={({ detail }) => setSelectedItem(detail.selectedItems[0])}
                            selectedItems={[selectedItem!]}
                            columnDefinitions={colDefinition}
                            items={data?.results!}
                            loading={isLoading}
                            loadingText="Loading resources"
                            sortingDisabled
                            variant={"container"}
                            empty={
                                <Box textAlign="center" color="inherit">
                                    <b>{"No users available."}</b>
                                </Box>
                            }
                            header={<h4>Select user</h4>}
                            pagination={
                                <Pagination
                                    currentPageIndex={data?.currentPage!}
                                    pagesCount={data?.totalPages!}
                                    onChange={(event) => clickTargetUserPage(event)}
                                    ariaLabels={{
                                        nextPageLabel: "Next page",
                                        previousPageLabel: "Previous page",
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