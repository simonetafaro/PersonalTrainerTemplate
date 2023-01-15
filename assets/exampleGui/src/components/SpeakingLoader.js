import React from "react";
import { ThreeDots } from 'react-loader-spinner'
const styles = {

    wrapper: {
        textAlign: "center",
        display: "block",
        margin: "0 auto",
        marginTop: "50px"
    }
}
class SpeakingLoader extends React.Component {

    render() {
        return (
            <div className="loader">
                <ThreeDots
                    height="80"
                    width="80"
                    radius="9"
                    color="#cbd5e4"
                    ariaLabel="three-dots-loading"
                    wrapperClass={{}}
                    wrapperStyle={styles.wrapper}
                    visible={true}
                />
            </div>

        );
    }
}

export default SpeakingLoader;