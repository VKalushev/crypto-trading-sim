import React from 'react';
import AssetsTable from './AssetsTable';

const Portfolio = ({ user }) => {
    return (
        <div>
            <h2 className="home-page-table-title">
                Your Balance: ${user.wallet.balance.toFixed(2)}
            </h2>
            <h2 className="home-page-table-title">Your Assets</h2>
            <AssetsTable assets={user.wallet.assets} />
        </div>
    );
};

export default Portfolio;
