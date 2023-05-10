import { withLocale } from "@/middleware/withLocale";
import { stackMiddlewares } from "@/middleware/stackMiddlewares";
import { withAuth } from "@/middleware/withAuth";

const middlewares = [withAuth, withLocale];

export default stackMiddlewares(middlewares);
