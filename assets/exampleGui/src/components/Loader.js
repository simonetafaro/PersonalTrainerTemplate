import React from "react";
import { BallTriangle } from 'react-loader-spinner'
const styles = {

    wrapper: {
        textAlign: "center",
        display: "block",
        margin: "0 auto",
        marginTop: "50px"
    }
}
class Loading extends React.Component {


    render() {
        return (
            <div className="loader">
                <BallTriangle
                    height={100}
                    width={100}
                    radius={5}
                    color="#4fa94d"
                    ariaLabel="ball-triangle-loading"
                    wrapperClass={{}}
                    wrapperStyle={styles.wrapper}
                    visible={true}
                />
            </div>

        );
    }
}

export default Loading;