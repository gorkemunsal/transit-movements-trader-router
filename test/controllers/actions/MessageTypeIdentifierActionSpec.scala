/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.actions

import base.SpecBase
import models.MessageRecipient
import models.MessageType
import models.requests.MessageRecipientRequest
import models.requests.RoutableRequest
import org.scalacheck.Gen
import org.scalatest.EitherValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MessageTypeIdentifierActionSpec extends SpecBase with ScalaFutures with EitherValues with ScalaCheckDrivenPropertyChecks {

  private val genXMessageType: Gen[MessageType] = Gen.oneOf(MessageType.validMessages)

  class Harness extends MessageTypeIdentifierAction(global) {
    def run[A](request: MessageRecipientRequest[A]): Future[Either[Result, RoutableRequest[A]]] = refine(request)
  }

  "MessageTypeIdentifierAction" - {
    "must return an BadRequest when the X-Message-Type is missing" in {
      def fakeRequest = MessageRecipientRequest(
        FakeRequest("", ""),
        MessageRecipient.fromHeaderValue("MDTP-ARR-1-1").get
      )

      val harness = new Harness

      val result = harness.run(fakeRequest)

      whenReady(result) {
        r =>
          r.isLeft mustBe true
          status(Future.successful(r.left.value)) mustEqual BAD_REQUEST
      }
    }

    "will process the action when a valid X-Message-Type is present" in {
      forAll(genXMessageType) {
        xMessageType =>
          def fakeRequest = MessageRecipientRequest(
            FakeRequest("", "").withHeaders(
              "X-Message-Type" -> xMessageType.code
            ),
            MessageRecipient.fromHeaderValue("MDTP-ARR-1-1").get
          )

          val action: Harness = new Harness()

          val result = action.run(fakeRequest)

          whenReady(result) {
            r =>
              r.isRight mustBe true
          }
      }
    }

    "will respond with NotImplemented when the X-Message-Type is not supported" in {
      def fakeRequest = MessageRecipientRequest(
        FakeRequest("", "").withHeaders(
          "X-Message-Type" -> "IE971"
        ),
        MessageRecipient.fromHeaderValue("MDTP-ARR-1-1").get
      )

      val action: Harness = new Harness()

      val result = action.run(fakeRequest)

      whenReady(result) {
        r =>
          r.isLeft mustBe true
          status(Future.successful(r.left.value)) mustEqual NOT_IMPLEMENTED
      }
    }
  }
}
