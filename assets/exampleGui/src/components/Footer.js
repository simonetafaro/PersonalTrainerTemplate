import React from "react";
import {
    Box
} from "./FooterStyles";

const Footer = () => {
    return (
        <Box>
            <footer className="footer">

                <h3 style={{
                    textAlign: "center",
                    marginTop: "0px",
                    fontSize: "18px"
                }}>
                    Train with Furhat
                </h3>


                <p>&copy;2022 DT2140 - Multimodal Interaction and Interfaces Course | Carlo Leopoldo Reinotti - Gabriele Carollo - Simon Toblad - Simone Tafaro</p>
            </footer>
        </Box >
        // <Box>

        //     <Container>
        //         <Row>
        //             <Column>
        //                 <h3 style={{
        //                     color: "green",
        //                     textAlign: "center",
        //                     marginTop: "-50px",
        //                     marginBottom: "20px"
        //                 }}>
        //                     DT2140 - Multimodal Interaction and Interfaces Course
        //                 </h3></Column>
        //             <Column>
        //                 <Heading>Authors</Heading>
        //                 <Row><FooterLink href="#">Carlo Reinotti</FooterLink>
        //                     <FooterLink href="#">Gabriele Carollo</FooterLink></Row>
        //                 <Row><FooterLink href="#">Simon Toblad</FooterLink>
        //                     <FooterLink href="#">Simone Tafaro</FooterLink></Row>
        //             </Column>

        //         </Row>
        //     </Container>
        // </Box>
    );
};
export default Footer;
