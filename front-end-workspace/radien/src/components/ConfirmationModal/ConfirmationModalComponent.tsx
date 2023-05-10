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

    const { header,
            body,
            modalVisible,
            setModalVisible,
            closeModalOnConfirm,
            confirmBehaviour } = props;

    return(
        <Modal
            visible={modalVisible}
            header={header}
            footer={
                <Box float="right">
                    <SpaceBetween direction="horizontal" size="xs">
                        <Button variant="link" onClick={() => setModalVisible(false)}>
                            Cancel
                        </Button>
                        <Button variant="primary" onClick={() => {
                            confirmBehaviour();
                            if(closeModalOnConfirm){
                                setModalVisible(false);
                            }}}>
                            Ok
                        </Button>
                    </SpaceBetween>
                </Box>
            }
        >
            {body}
        </Modal>
    );
}