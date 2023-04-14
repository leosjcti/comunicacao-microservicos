import brcypt from "bcrypt";
import User from "../../modules/model/User.js";

export async function createInitialData() {
    try {
        await User.sync({ force: true });
        let password = await brcypt.hash("123456", 10);

        await User.create ({
            name: "User Teste 1",
            email: "teste1@gmail.com",
            password: password
        });
        await User.create ({
            name: "User Teste 2",
            email: "teste2@gmail.com",
            password: password
        });
    } catch (error) {
        console.log(error);
    }    
}