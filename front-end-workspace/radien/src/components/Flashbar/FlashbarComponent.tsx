import React, {useContext} from "react";
import {Flashbar} from "@cloudscape-design/components";
import {RadienContext} from "@/context/RadienContextProvider";

export default function FlashbarComponent() {
    const context = useContext(RadienContext);
    return (
        <div className={"flash-bar--container"}>
            <Flashbar items={context.values} />
        </div>
    );
}