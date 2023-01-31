import React, {useContext} from "react";
import {Flashbar} from "@cloudscape-design/components";
import {FlashbarContext} from "@/context/FlashbarContext";

export default function FlashbarComponent() {
    const context = useContext(FlashbarContext);
    return (
        <Flashbar items={context.values} />
    );
}