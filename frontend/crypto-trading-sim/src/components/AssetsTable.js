import React from 'react';
import './../styles/crypto-table.css';

const AssetsTable = ({ assets }) => {
    if (!assets || assets.length === 0) {
        return <p>You have no assets yet.</p>;
    }

    return (
        <table className="crypto-table">
            <thead>
                <tr>
                    <th>Asset</th>
                    <th>Amount</th>
                </tr>
            </thead>
            <tbody>
                {assets.map((asset) => (
                    <tr key={asset.symbol}>
                        <td>{asset.symbol}</td>
                        <td>{asset.amount}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export default AssetsTable;
