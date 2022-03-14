package funstack.backend

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import cats.effect.IO
import cats.implicits._

@js.native
trait FunDevEnvironment extends js.Object {
  def send_subscription(@annotation.nowarn subscriptionKey: String, @annotation.nowarn data: String): Unit = js.native
}

case class FunConfig(
  devEnvironment: Option[FunDevEnvironment],
  cognitoUserPoolId: Option[String],
  eventsSnsTopic: Option[String],
)

case class Config(
  fun: FunConfig,
  environment: js.Dictionary[String],
) {
  def environmentTyped[Env <: js.Any]: Env = environment.asInstanceOf[Env]

}

object Config {
  import js.Dynamic.{global => g}

  def load() = Config(
    fun = FunConfig(
    // lambda-server sets fun_dev_environment to send events in development case. We fallback to this, incase the environment variables from the terraform module are not set.
    devEnvironment = Either.catchNonFatal(g.fun_dev_environment.asInstanceOf[js.UndefOr[FunDevEnvironment]].toOption).toOption.flatten,
    cognitoUserPoolId = g.process.env.FUN_AUTH_COGNITO_USER_POOL_ID.asInstanceOf[js.UndefOr[String]].toOption,
    eventsSnsTopic = g.process.env.FUN_EVENTS_SNS_OUTPUT_TOPIC.asInstanceOf[js.UndefOr[String]].toOption,
    ),
    environment = g.process.env.asInstanceOf[js.Dictionary[String]],
  )
}
