import React, {useContext, useState} from "react";
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
import {useRouter} from "next/router";
import {RadienContext} from "@/context/RadienContextProvider";

interface SelectedItemDetails<T> {
    selectedItem: T | undefined,
    setSelectedItem: (value: T) => void
}

interface DeleteActionProps {
    deleteLabel?: string,
    deleteConfirmationText: string,
    deleteAction?: (UseMutateFunction<AxiosResponse<any, any>, unknown, any, unknown>)
}

interface CreateActionProps {
    hideCreate?: boolean,
    createLabel?: string,
    createAction?: () => void
}

interface ViewActionDetails {
    viewComponent?: React.ReactNode
}

interface EmptyProps {
    emptyMessage?: string,
    emptyActionLabel?: string,
}

export interface PaginatedTableProps<T> {
    tableHeader: string,
    tableVariant?: 'embedded' | 'container' | 'stacked' | 'full-page'
    queryKey: string,
    columnDefinitions: TableProps.ColumnDefinition<T>[],
    getPaginated: (pageNumber?: number, pageSize?: number) => Promise<AxiosResponse<Page<T>, Error>>,
    selectedItemDetails: SelectedItemDetails<T>
    viewActionProps: ViewActionDetails,
    createActionProps: CreateActionProps,
    deleteActionProps: DeleteActionProps,
    emptyProps: EmptyProps,
}

export default function PaginatedTable<T>(props: PaginatedTableProps<T>) {
    const {
        tableHeader,
        tableVariant,
        columnDefinitions,
        queryKey,
        getPaginated,
        selectedItemDetails: {selectedItem, setSelectedItem},
        deleteActionProps: {deleteLabel, deleteConfirmationText, deleteAction},
        createActionProps: {createLabel, hideCreate, createAction},
        viewActionProps: { viewComponent },
        emptyProps: {emptyMessage, emptyActionLabel }
    } = props;
    const { userInSession } = useContext(RadienContext);
    const [ currentPage, setCurrentPage ] = useState<number>(1);
    const [ pageSize, setPageSize ] = useState<number>(10);
    const [ deleteModalVisible, setDeleteModalVisible ] = useState(false);
    const [ viewModalVisible, setViewModalVisible ] = useState(false);
    const router = useRouter();
    const { isLoading, data } = useQuery([queryKey, currentPage], () => getPaginated(currentPage, pageSize))


    const clickTargetUserPage = async (event: NonCancelableCustomEvent<PaginationProps.ChangeDetail>) => {
        let targetPage = event.detail.currentPageIndex;
        setCurrentPage(targetPage);
    }

    const createResource = () => {
        if (createAction) {
            createAction();
        }
    }

    return (
        <>
            <Table
                selectionType={"single"}
                onSelectionChange={ ({ detail }) => setSelectedItem(detail.selectedItems[0]) }
                selectedItems={[selectedItem!]}
                columnDefinitions={columnDefinitions}
                items={data?.data?.results!}
                loading={isLoading}
                loadingText="Loading resources"
                sortingDisabled
                variant={tableVariant || "container"}
                empty={
                    <Box textAlign="center" color="inherit">
                        <b>{emptyMessage || "No resources to display."}</b>
                        <Box
                            padding={{ bottom: "s", top: "m" }}
                            variant="p"
                            color="inherit"
                        >
                        <Button onClick={createResource}>{emptyActionLabel || "Create resource"}</Button>
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
                            <Button variant="primary" onClick={() => {
                                if(deleteAction) {
                                    deleteAction({tenantId: selectedItem ? (selectedItem as RadienModel).id! : -1, userId: userInSession?.id});
                                }
                                setDeleteModalVisible(false)
                            }}>Ok</Button>
                        </SpaceBetween>
                    </Box>
                }
                header={deleteLabel}>
                {deleteConfirmationText}
            </Modal>
            <Modal
                onDismiss={() => setViewModalVisible(false)}
                visible={viewModalVisible}
                closeAriaLabel="Close modal"
                footer={
                    <Box float="right">
                        <SpaceBetween direction="horizontal" size="xs">
                            <Button variant="primary" onClick={() => { setViewModalVisible(false)}}>Ok</Button>
                        </SpaceBetween>
                    </Box>
                }
                header="View">
                {viewComponent}
            </Modal>


            <div className="flex justify-end py-6 gap-2">
                { !hideCreate && <Button onClick={createResource} variant={"primary"}>{createLabel || 'Create'}</Button> }
                {viewComponent && <Button disabled={!selectedItem} onClick={() => setViewModalVisible(true)}>View</Button>}
                <Button onClick={() => setDeleteModalVisible(true)} disabled={!selectedItem}>{deleteLabel || 'Delete'}</Button>
            </div>
        </>
    )
}