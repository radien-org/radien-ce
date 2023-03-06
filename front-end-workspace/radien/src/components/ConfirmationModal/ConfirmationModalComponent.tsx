import {Box, Button, Modal, SpaceBetween} from "@cloudscape-design/components";

export interface ConfirmationModalProps{
    header: string;
    body: string;
    modalVisible: boolean;

    setModalVisible: (visible : boolean) => void;
    closeModalOnConfirm?: boolean;
    confirmBehaviour: () => void;
}

export function ConfirmationModalComponent(props: ConfirmationModalProps) {

    const {modalVisible, setModalVisible} = props;

    return(
        <Modal
            visible={modalVisible}
            header={props.header}
            footer={
                <Box float="right">
                    <SpaceBetween direction="horizontal" size="xs">
                        <Button variant="link" onClick={() => setModalVisible(false)}>
                            Cancel
                        </Button>
                        <Button variant="primary" onClick={() => {
                            props.confirmBehaviour();
                            if(props.closeModalOnConfirm){
                                setModalVisible(false);
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