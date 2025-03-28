import React from 'react';
import PropTypes from 'prop-types';

import { FileUploadForm } from './FileUploadForm';
export class FileUploadPanel extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            files: []
        };
    }
    addFile (file) {
        this.setState((state, props) => {
            return { ...state, files: state.files.concat([file]) };
        });
    }
    removeFile (file) {
        this.setState((state, props) => {
            let files = state.files.filter(f => f !== file);
            return { ...state, files };
        });
    }
    onRenameFile (file, name) {
        this.setState((state, props) => {
            let modified = state.files.find(f => f === file);
            modified.locale = name;
            return { ...state };
        });
    }
    resetFiles () {
        this.setState((state, props) => {
            return { ...state, files: [] };
        });
    }
    onSubmit (e) {
        e.preventDefault();
        this.props.onSubmit(this.state, () => this.resetFiles());
    }
    hasContent () {
        return this.state.files.length;
    }
    render () {
        return (
            <FileUploadForm
                files={this.state.files}
                onSubmit={e => this.onSubmit(e)}
                onRemoveFile={file => this.removeFile(file)}
                onRenameFile={(file, name) => this.onRenameFile(file, name)}
                onAddFile={file => this.addFile(file)}
            >
                <input type="submit" disabled={!this.hasContent()} value="Lisää tiedostot" />
            </FileUploadForm>
        );
    }
}

FileUploadPanel.propTypes = {
    onSubmit: PropTypes.func
};
