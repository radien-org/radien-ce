import React from 'react';
import {Session} from "next-auth";
import Card from "@/components/Card/Card";

export default function ServiceDashboard() {
    const cards = [
        {
         title: "User Management",
         description: "This service allows you to see and check your users data as so as delete them.",
         href: "/system/userManagement",
        },
        {
        title: "Role Management",
        description: "In here you can create and assign roles to a specific user in a specific tenant.",
        href: "/system/roleManagement",
        },
        {
        title: "Permission Management",
        description: "Which permissions are going to be allowed? In here you can define them.",
        href: "/system/permissionManagement",
        }
    ];


    return (
        <div className="container my-12 mx-auto px-4 md:px-12">
            <div className="flex flex-wrap -mx-1 lg:-mx-4">

                {cards.map(c =>  <Card title={c.title} description={c.description} href={c.href} /> ) }

            </div>
        </div>
);
}