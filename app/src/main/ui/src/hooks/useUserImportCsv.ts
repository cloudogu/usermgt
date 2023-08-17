import CSV from "comma-separated-values";
import {useEffect, useState} from "react";

export default function useUserImportCsv(selectedFile: FileList | undefined) {
    const [header, setHeader] = useState([""]);
    const [rows, setRows] = useState([[""]]);
    const [fileSize, setFileSize] = useState(0);
    const [fileType, setFileType] = useState("");

    useEffect(() => {
        const file = selectedFile?.item(0) as File;
        if (file && file.type === "text/csv") {
            file.text().then(text => {
                const csv = new CSV(text);
                const result = csv.parse();
                const length = result?.length ?? 0;
                if (length > 0) {
                    setHeader(result[0]);
                    result.shift();
                    setRows([...result]);
                    setFileSize(file.size);
                    setFileType(file.type);
                }
            });
        } else {
            setHeader([]);
            setRows([]);
            setFileSize(0);
            setFileType("");
        }
    }, [selectedFile]);

    return {
        file: (fileSize === 0) ? undefined : {
            size: fileSize,
            type: fileType,
            header: header,
            rows: rows,
        }
    };
}