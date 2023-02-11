"use client";

import React, { useContext, useState } from "react";
import { Page, RadienModel } from "radien";
import { AxiosResponse } from "axios";
import { Box, Button, Header, Modal, NonCancelableCustomEvent, Pagination, PaginationProps, SpaceBetween, TableProps } from "@cloudscape-design/components";
import { UseMutateFunction, useQuery, UseQueryResult } from "react-query";
import { RadienContext } from "@/context/RadienContextProvider";
import dynamic from "next/dynamic";
import { TableForwardRefType } from "@cloudscape-design/components/table/interfaces";

interface DeleteActionProps<T> {
    deleteLabel?: string;
    deleteConfirmationText: (val?: T) => string;
    deleteAction?: UseMutateFunction<AxiosResponse<any, any>, unknown, any, unknown>;
}

interface CreateActionProps {
    hideCreate?: boolean;
    createLabel?: string;
    createAction?: () => void;
}

interface AggregateProps<T> {
    aggregators?: {
        mapper: (foreignData: any, tableData: T[]) => T[];
        loader: () => Promise<AxiosResponse<any, any>>;
        queryKey: string[];
    }[];
}

interface ViewActionDetails {
    ViewComponent?: React.FunctionComponent<{ data: any }>;
    viewTitle?: string;
}

interface EmptyProps {
    emptyMessage?: string;
    emptyActionLabel?: string;
}

export interface DeleteParams {
    tenantId: string;
    userId: string;
}

export interface PaginatedTableProps<T> {
    tableHeader: string;
    tableVariant?: "embedded" | "container" | "stacked" | "full-page";
    queryKey: string;
    columnDefinitions: TableProps.ColumnDefinition<T>[];
    getPaginated: (pageNumber?: number, pageSize?: number) => Promise<AxiosResponse<Page<T>, Error>>;
    viewActionProps: ViewActionDetails;
    createActionProps: CreateActionProps;
    deleteActionProps: DeleteActionProps<T>;
    emptyProps: EmptyProps;
    aggregateProps?: AggregateProps<T>;
}

const Table = dynamic(() => import("@cloudscape-design/components/table"), { ssr: false }) as TableForwardRefType;

export default function PaginatedTable<T>(props: PaginatedTableProps<T>) {
    const {
        tableHeader,
        tableVariant,
        columnDefinitions,
        queryKey,
        getPaginated,
        deleteActionProps: { deleteLabel, deleteConfirmationText, deleteAction },
        createActionProps: { createLabel, hideCreate, createAction },
        viewActionProps: { ViewComponent, viewTitle },
        emptyProps: { emptyMessage, emptyActionLabel },
        aggregateProps,
    } = props;
    const { userInSession } = useContext(RadienContext);
    const [pageSize, setPageSize] = useState<number>(10);
    const [deleteModalVisible, setDeleteModalVisible] = useState(false);
    const [selectedItem, setSelectedItem] = useState<T>();
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [viewModalVisible, setViewModalVisible] = useState(false);
    const { isLoading, data, refetch } = useQuery([queryKey, currentPage], () => getPaginated(currentPage, pageSize));
    let loadedData: { query: UseQueryResult; mapper: (foreignData: any, tableData: T[]) => T[] }[] | undefined;

    if (aggregateProps) {
        loadedData = aggregateProps.aggregators?.map((aggregator) => {
            return {
                query: useQuery(aggregator.queryKey, aggregator.loader),
                mapper: aggregator.mapper,
            };
        });
        if (loadedData && loadedData.length > 0) {
            loadedData.forEach((refData) => {
                const { query, mapper } = refData;
                if (query.data && data && data.data) {
                    data.data.results = mapper(query.data, data.data.results);
                }
            });
        }
    }

    const loading =
        isLoading ||
        (aggregateProps &&
            aggregateProps.aggregators &&
            aggregateProps.aggregators.length > 0 &&
            loadedData &&
            loadedData.length > 0 &&
            loadedData.some((query: any) => query.isLoading));

    const clickTargetUserPage = async (event: NonCancelableCustomEvent<PaginationProps.ChangeDetail>) => {
        let targetPage = event.detail.currentPageIndex;
        setCurrentPage(targetPage);
    };

    const createResource = () => {
        if (createAction) {
            createAction();
        }
    };

    const onDelete = async () => {
        if (deleteAction) {
            await deleteAction({ tenantId: selectedItem ? (selectedItem as RadienModel).id! : -1, userId: userInSession?.id });
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
                items={data?.data?.results!}
                loading={loading}
                loadingText="Loading resources"
                sortingDisabled
                variant={tableVariant || "container"}
                empty={
                    <Box textAlign="center" color="inherit">
                        <b>{emptyMessage || "No resources to display."}</b>
                        <Box padding={{ bottom: "s", top: "m" }} variant="p" color="inherit">
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
                                Ok
                            </Button>
                        </SpaceBetween>
                    </Box>
                }
                header={viewTitle || "View"}>
                {ViewComponent && <ViewComponent data={selectedItem} />}
            </Modal>

            <div className="flex justify-end py-6 gap-2">
                {!hideCreate && (
                    <Button onClick={createResource} variant={"primary"}>
                        {createLabel || "Create"}
                    </Button>
                )}
                {ViewComponent && (
                    <Button disabled={!selectedItem} onClick={() => setViewModalVisible(true)}>
                        View
                    </Button>
                )}
                <Button onClick={() => setDeleteModalVisible(true)} disabled={!selectedItem}>
                    {deleteLabel || "Delete"}
                </Button>
            </div>
        </>
    );
}
