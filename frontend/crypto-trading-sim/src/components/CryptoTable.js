import React from 'react';
import './../styles/crypto-table.css';

const CryptoTable = ({ topPairsInfo, prices, handleTrade }) => {
    return (
        <table className="crypto-table">
            <thead>
                <tr>
                    <th>Rank</th>
                    <th>Symbol</th>
                    <th>Price (USD)</th>
                    <th>24h USD Volume</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                {topPairsInfo.map((pair, index) => (
                    <tr key={pair.wsname}>
                        <td>{index + 1}</td>
                        <td>{pair.wsname}</td>
                        <td>
                            {prices[pair.wsname]
                                ? parseFloat(prices[pair.wsname]).toFixed(2)
                                : 'Loading...'}
                        </td>
                        <td>
                            {pair.usdValue
                                ? pair.usdValue.toLocaleString(undefined, {
                                      maximumFractionDigits: 0,
                                  })
                                : 'Loading...'}
                        </td>
                        <td>
                            <button
                                className="trade-btn buy"
                                onClick={() => handleTrade('buy', pair, 100)}
                            >
                                Buy
                            </button>
                            <button
                                className="trade-btn sell"
                                onClick={() => handleTrade('sell', pair, 100)}
                            >
                                Sell
                            </button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export default CryptoTable;
