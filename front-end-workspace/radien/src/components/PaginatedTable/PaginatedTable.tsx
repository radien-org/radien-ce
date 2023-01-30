import React, {useState} from "react";
import {Page, RadienModel} from "radien";
import {AxiosResponse} from "axios";
import {
    Box,
    Button,
    Header, Modal,
    NonCancelableCustomEvent,
    Pagination,
    PaginationProps,
    SpaceBetween,
    Table, TableProps
} from "@cloudscape-design/components";
import {UseMutateFunction, useQuery} from "react-query";

export interface PaginatedTableProps<T> {
    queryKey: string,
    getPaginated: (pageNumber?: number, pageSize?: number) => Promise<AxiosResponse<Page<T>, Error>>,
    columnDefinitions: TableProps.ColumnDefinition<T>[],
    deleteConfirmationText: string,
    deleteAction: (UseMutateFunction<AxiosResponse<any, any>, unknown, number, unknown>),
    tableHeader: string,
    hideCreate?: boolean
    emptyMessage?: string,
    emptyAction?: string,

}

export default function PaginatedTable<T>(props: PaginatedTableProps<T>) {
    const {
        queryKey,
        getPaginated,
        columnDefinitions,
        deleteConfirmationText,
        deleteAction,
        tableHeader,
        hideCreate,
        emptyMessage,
        emptyAction } = props;
    const [ currentPage, setCurrentPage ] = useState<number>(1);
    const [ pageSize, setPageSize ] = useState<number>(10);
    const [ selectedItem, setSelectedItem ] = useState<T>();
    const [ deleteModalVisible, setDeleteModalVisible ] = useState(false);
    const { isLoading, data } = useQuery([queryKey, currentPage], () => getPaginated(currentPage, pageSize))


    const clickTargetUserPage = async (event: NonCancelableCustomEvent<PaginationProps.ChangeDetail>) => {
        let targetPage = event.detail.currentPageIndex;
        setCurrentPage(targetPage);
    }

    return (
        <>
            <Box padding={"xl"}>
                <Table
                    selectionType={"single"}
                    onSelectionChange={ ({ detail }) => setSelectedItem(detail.selectedItems[0]) }
                    selectedItems={[selectedItem!]}
                    columnDefinitions={columnDefinitions}
                    items={data?.data?.results!}
                    loading={isLoading}
                    loadingText="Loading resources"
                    sortingDisabled
                    empty={
                        <Box textAlign="center" color="inherit">
                            <b>{emptyMessage || "No resources to display."}</b>
                            <Box
                                padding={{ bottom: "s", top: "m" }}
                                variant="p"
                                color="inherit"
                            >
                            <Button>{emptyAction || "Create resource"}</Button>
                            </Box>
                        </Box>
                    }
                    header={<Header key={queryKey}> {tableHeader} </Header>}
                    pagination={
                        <Pagination
                            currentPageIndex={data?.data?.currentPage!}
                            pagesCount={data?.data?.totalPages!}
                            onChange={(event) => clickTargetUserPage(event)}
                            ariaLabels={{
                                nextPageLabel: "Next page",
                                previousPageLabel: "Previous page",
                                pageLabel: pageNumber => `Page ${pageNumber} of all pages` }}
                        />
                    }
                />
                <Modal
                    onDismiss={() => setDeleteModalVisible(false)}
                    visible={deleteModalVisible}
                    closeAriaLabel="Close modal"
                    footer={
                        <Box float="right">
                            <SpaceBetween direction="horizontal" size="xs">
                                <Button variant="link" onClick={() => setDeleteModalVisible(false)}>Cancel</Button>
                                <Button variant="primary" onClick={() => { deleteAction(selectedItem ? (selectedItem as RadienModel).id! : -1); setDeleteModalVisible(false)}}>Ok</Button>
                            </SpaceBetween>
                        </Box>
                    }
                    header="Delete">
                    {deleteConfirmationText}
                </Modal>
            </Box>


            <div className="flex justify-end px-24 py-6 gap-1">
                { !hideCreate && <Button variant={"primary"}>Create</Button> }
                <Button disabled={!selectedItem}>View</Button>
                <Button onClick={() => setDeleteModalVisible(true)} disabled={!selectedItem}>Delete</Button>
            </div>
        </>
    )
}