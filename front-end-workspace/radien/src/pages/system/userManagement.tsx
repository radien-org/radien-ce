"use client";

import React, {useEffect, useState} from "react";
import {User, Page} from "radien";
import axios from "axios";
import {
    Box,
    Button,
    Header, Modal,
    NonCancelableCustomEvent,
    Pagination,
    PaginationProps,
    SpaceBetween,
    Table
} from "@cloudscape-design/components";


React.useLayoutEffect = React.useEffect;

export default function UserManagement() {
    const [selectedItem, setSelectedItem] = useState<User>();
    const [deleteModalVisible, setDeleteModalVisible] = useState(false);
    const [ usersPage, setUsersPage ] = useState<Page<User>>();

    const clickTargetUserPage = async (event: NonCancelableCustomEvent<PaginationProps.ChangeDetail>) => {
        let targetPage = event.detail.currentPageIndex;
        getUserPage(targetPage)
            .then(result => {
                setUsersPage(result.data);
            })
    }

    const getUserPage = async (pageNumber: number = 1, pageSize: number = 10) => {
        return await axios.get("/api/user/getAll", {
            params: {
                page: pageNumber,
                pageSize: pageSize
            }
        });
    }

    useEffect(() => {
        getUserPage()
            .then(result => {
                setUsersPage(result.data);
            })
    }, [])

    return (
        <>
            <Box padding={"xl"}>
                <Table
                    selectionType={"single"}
                    onSelectionChange={({ detail }) =>
                        setSelectedItem(detail.selectedItems[0])
                    }
                    selectedItems={[selectedItem]}
                    columnDefinitions={[
                        {
                            id: "logon",
                            header: "Username",
                            cell: item => item?.logon || "-",
                            sortingField: "logon"
                        },{
                            id: "firstname",
                            header: "First Name",
                            cell: item => item?.firstname || "-",
                            sortingField: "firstname"
                        },
                        {
                            id: "lastname",
                            header: "Last Name",
                            cell: item => item?.lastname || "-",
                            sortingField: "lastname"
                        },
                        {
                            id: "userEmail",
                            header: "User Email",
                            cell: item => item?.userEmail || "-",
                            sortingField: "userEmail"
                        }
                    ]}
                    items={usersPage?.results!}
                    loadingText="Loading resources"
                    sortingDisabled
                    empty={
                        <Box textAlign="center" color="inherit">
                            <b>No resources</b>
                            <Box
                                padding={{ bottom: "s" }}
                                variant="p"
                                color="inherit"
                            >
                                No resources to display.
                            </Box>
                            <Button>Create resource</Button>
                        </Box>
                    }
                    header={<Header> User Management </Header>}
                    pagination={
                        <Pagination
                            currentPageIndex={usersPage?.currentPage!}
                            pagesCount={usersPage?.totalPages!}
                            onChange={(event) => clickTargetUserPage(event)}
                            ariaLabels={{
                                nextPageLabel: "Next page",
                                previousPageLabel: "Previous page",
                                pageLabel: pageNumber =>
                                    `Page ${pageNumber} of all pages`
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
                                <Button variant="link" onClick={() => setDeleteModalVisible(false)}>Cancel</Button>
                                <Button variant="primary">Ok</Button>
                            </SpaceBetween>
                        </Box>
                    }
                    header="Delete User"
                >
                    Are you sure you would like to delete {selectedItem?.firstname} {selectedItem?.lastname}
                </Modal>
            </Box>


            <div className="flex justify-end px-24 py-6 gap-1">
                <Button variant={"primary"}>Create</Button>
                <Button disabled={!selectedItem}>View</Button>
                <Button onClick={() => setDeleteModalVisible(true)} disabled={!selectedItem}>Delete</Button>
            </div>
        </>
    )
}