import {Spinner} from "@cloudscape-design/components";

export const Loader = () => {
    return (
        <div className="loader-container">
            <Spinner size={"large"} variant={"normal"}/>
        </div>
    );
};