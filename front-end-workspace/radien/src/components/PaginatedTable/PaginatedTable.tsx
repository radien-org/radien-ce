"use client";

import React, { useContext, useEffect, useState } from "react";
import {Page, RadienModel, User} from "radien";
import { AxiosResponse } from "axios";
import { Box, Button, Header, Modal, NonCancelableCustomEvent, Pagination, PaginationProps, SpaceBetween, TableProps } from "@cloudscape-design/components";
import {UseMutateFunction, useQuery, UseQueryResult} from "react-query";
import { RadienContext } from "@/context/RadienContextProvider";
import dynamic from "next/dynamic";
import { TableForwardRefType } from "@cloudscape-design/components/table/interfaces";
import {QueryKeys} from "@/consts";

interface DeleteActionProps<T> {
    deleteLabel?: string;
    deleteNestedObj?: string;
    deleteConfirmationText: (val?: T) => string;
    deleteAction?: UseMutateFunction<AxiosResponse<any, any>, unknown, any, unknown>;
    onDeleteSuccess?: () => void;
}

interface CreateActionProps<T> {
    hideCreate?: boolean;
    createLabel?: string;
    createAction?: (selectedValue?: T) => void;
    createButtonType?: "normal" | "primary" | "link" | "icon" | "inline-icon";
}

interface ViewActionDetails {
    ViewComponent?: React.FunctionComponent<{ data: any }>;
    viewTitle?: string;
    viewLabel?: string;
    viewConfirmLabel?: string;
}

interface EmptyProps {
    emptyMessage?: string;
    emptyActionLabel?: string;
}

export interface DeleteParams {
    objectId: string;
    userId: string;
}

export interface PaginatedTableProps<T> {
    tableHeader: string;
    tableVariant?: "embedded" | "container" | "stacked" | "full-page";
    queryKey: string;
    columnDefinitions: TableProps.ColumnDefinition<T>[];
    getPaginated: ((pageNumber: number , pageSize: number)=> Promise<AxiosResponse<Page<T>, T>>);
    onSuccessfulFetch?: (data: Page<T>) => void;
    onSelectAction?: (item: T) => void;
    viewActionProps: ViewActionDetails;
    createActionProps: CreateActionProps<T>;
    deleteActionProps: DeleteActionProps<T>;
    emptyProps: EmptyProps;
    manipulationDisableCondition?: boolean;
}

const Table = dynamic(() => import("@cloudscape-design/components/table"), { ssr: false }) as TableForwardRefType;

export default function PaginatedTable<T>(props: PaginatedTableProps<T>) {
    const {
        tableHeader,
        tableVariant,
        columnDefinitions,
        manipulationDisableCondition = false,
        queryKey,
        onSelectAction,
        getPaginated,
        onSuccessfulFetch,
        deleteActionProps: { deleteLabel, deleteNestedObj, deleteConfirmationText, deleteAction, onDeleteSuccess },
        createActionProps: { createLabel, createButtonType, hideCreate, createAction },
        viewActionProps: { ViewComponent, viewTitle, viewLabel, viewConfirmLabel },
        emptyProps: { emptyMessage, emptyActionLabel },
    } = props;
    const {
        userInSession,
        activeTenant: { data: activeTenantData },
    } = useContext(RadienContext);
    const [pageSize, setPageSize] = useState<number>(10);
    const [deleteModalVisible, setDeleteModalVisible] = useState(false);
    const [selectedItem, setSelectedItem] = useState<T>();
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [viewModalVisible, setViewModalVisible] = useState(false);
    const { isLoading, data, refetch } =  useQuery<Page<T>, Error>(
        [queryKey, currentPage],
        async () => {
            const res = await getPaginated(currentPage, pageSize);
            return res.data;
        },
        { refetchInterval: 300000, onSuccess: (data) => onSuccessfulFetch && onSuccessfulFetch(data)},
    );

    useEffect(() => {
        if (onSelectAction) {
            // @ts-ignore
            onSelectAction(selectedItem);
        }   
    }, [selectedItem]);

    const clickTargetUserPage = async (event: NonCancelableCustomEvent<PaginationProps.ChangeDetail>) => {
        let targetPage = event.detail.currentPageIndex;
        setCurrentPage(targetPage);
    };

    const createResource = () => {
        if (createAction) {
            createAction(selectedItem ? selectedItem : undefined);
        }
    };

    const onDelete = async () => {
        if (deleteAction && selectedItem) {
            const field = deleteNestedObj ? ((selectedItem as any)[deleteNestedObj] as RadienModel).id : (selectedItem as RadienModel).id;
            deleteAction(
                { objectId: field, userId: userInSession?.id, tenantId: activeTenantData?.tenantId! },
                {
                    onSuccess: () => {
                        if (onDeleteSuccess) {
                            onDeleteSuccess();
                            setSelectedItem(undefined);
                        }
                    },
                }
            );
            await refetch();
        }
        setDeleteModalVisible(false);
    };

    return (
        <>
            <Table
                selectionType={"single"}
                onSelectionChange={({ detail }) => setSelectedItem(detail.selectedItems[0])}
                selectedItems={[selectedItem!]}
                columnDefinitions={columnDefinitions}
                items={data?.results!}
                loading={isLoading}
                loadingText="Loading resources"
                sortingDisabled
                variant={tableVariant || "container"}
                empty={
                    <Box textAlign="center" color="inherit">
                        <b>{emptyMessage || "No resources to display."}</b>
                        <Box padding={{ bottom: "s", top: "m" }} variant="p" color="inherit">
                            {emptyActionLabel && <Button onClick={createResource}>emptyActionLabel</Button>}
                        </Box>
                    </Box>
                }
                header={<Header key={queryKey}> {tableHeader} </Header>}
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
            <Modal
                onDismiss={() => setDeleteModalVisible(false)}
                visible={deleteModalVisible}
                closeAriaLabel="Close modal"
                footer={
                    <Box float="right">
                        <SpaceBetween direction="horizontal" size="xs">
                            <Button variant="link" onClick={() => setDeleteModalVisible(false)}>
                                Cancel
                            </Button>
                            <Button variant="primary" onClick={() => onDelete()}>
                                Ok
                            </Button>
                        </SpaceBetween>
                    </Box>
                }
                header={deleteLabel}>
                {deleteConfirmationText(selectedItem)}
            </Modal>
            <Modal
                onDismiss={() => setViewModalVisible(false)}
                visible={viewModalVisible}
                closeAriaLabel="Close modal"
                footer={
                    <Box float="right">
                        <SpaceBetween direction="horizontal" size="xs">
                            <Button
                                variant="primary"
                                onClick={() => {
                                    setViewModalVisible(false);
                                }}>
                                {viewConfirmLabel || "Ok"}
                            </Button>
                        </SpaceBetween>
                    </Box>
                }
                header={viewTitle || "View"}>
                {ViewComponent && <ViewComponent data={selectedItem} />}
            </Modal>

            <div className="flex justify-end py-6 gap-2">
                {!hideCreate && (
                    <Button disabled={manipulationDisableCondition} onClick={createResource} variant={createButtonType || "primary"}>
                        {createLabel || "Create"}
                    </Button>
                )}
                {ViewComponent && (
                    <Button disabled={!selectedItem} onClick={() => setViewModalVisible(true)}>
                        {viewLabel || "View"}
                    </Button>
                )}
                <Button onClick={() => setDeleteModalVisible(true)} disabled={!selectedItem || manipulationDisableCondition}>
                    {deleteLabel || "Delete"}
                </Button>
            </div>
        </>
    );
}
