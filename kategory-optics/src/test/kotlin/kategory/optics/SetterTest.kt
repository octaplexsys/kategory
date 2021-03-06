package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Eq
import kategory.SetterLaws
import kategory.UnitSpec
import kategory.genFunctionAToB
import kategory.genOption
import kategory.getOrElse
import kategory.left
import kategory.right
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class SetterTest : UnitSpec() {

    init {
        testLaws(
                SetterLaws.laws(
                        setter = Setter.id(),
                        aGen = Gen.int(),
                        bGen = Gen.int(),
                        funcGen = genFunctionAToB(Gen.int()),
                        EQA = Eq.any()
                ) + SetterLaws.laws(
                        setter = tokenSetter,
                        aGen = TokenGen,
                        bGen = Gen.string(),
                        funcGen = genFunctionAToB(Gen.string()),
                        EQA = Eq.any()
                ) + SetterLaws.laws(
                        setter = Setter.fromFunctor(),
                        aGen = genOption(TokenGen),
                        bGen = Gen.string(),
                        funcGen = genFunctionAToB(Gen.string()),
                        EQA = Eq.any()
                )
        )

        "Joining two lenses together with same target should yield same result" {
            val userTokenStringSetter = userSetter compose tokenSetter
            val joinedSetter = tokenSetter.choice(userTokenStringSetter)
            val oldValue = "oldValue"
            val token = Token(oldValue)
            val user = User(token)

            forAll({ value: String ->
                joinedSetter.set(token.left(), value).swap().getOrElse { Token("Wrong value") }.value ==
                        joinedSetter.set(user.right(), value).getOrElse { User(Token("Wrong value")) }.token.value
            })
        }

    }

}