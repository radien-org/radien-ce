import { Container } from "@cloudscape-design/components";
import { useRouter } from "next/router";

export default function ConfirmationPage() {
    const router = useRouter();
    const { message } = router.query;
    return (
        <Container>
            <p>{message || "Email successfully changed!"}</p>
        </Container>
    );
}
