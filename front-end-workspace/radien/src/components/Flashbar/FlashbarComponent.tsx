import React, {useContext} from "react";
import {Flashbar} from "@cloudscape-design/components";
import {RadienContext} from "@/context/RadienContextProvider";

export default function FlashbarComponent() {
    const context = useContext(RadienContext);
    return (
        <Flashbar items={context.values} />
    );
}