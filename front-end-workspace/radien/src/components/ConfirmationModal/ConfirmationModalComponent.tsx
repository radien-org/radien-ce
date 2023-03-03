import {Box, Button, Modal, SpaceBetween} from "@cloudscape-design/components";

export interface ConfirmationModalProps{
    header: string;
    body: string;
    visibilityController: boolean;
    closeModalOnConfirm?: boolean;
    confirmBehaviour: () => void;
}

export function ConfirmationModalComponent(props: ConfirmationModalProps) {

    return(
        <Modal
            visible={props.visibilityController}
            header={props.header}
            footer={
                <Box float="right">
                    <SpaceBetween direction="horizontal" size="xs">
                        <Button variant="link" onClick={() => props.visibilityController = false}>
                            Cancel
                        </Button>
                        <Button variant="primary" onClick={() => {
                            props.confirmBehaviour;
                            if(props.closeModalOnConfirm){
                                props.visibilityController = false;
                            }}}>
                            Ok
                        </Button>
                    </SpaceBetween>
                </Box>
            }
        >
            {props.body}
        </Modal>
    );
}